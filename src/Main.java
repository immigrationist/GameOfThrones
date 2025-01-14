import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Guild> guilds = loadGuildData();

        System.out.println("All Guilds:");
        guilds.forEach(System.out::println);

        List<Adventurer> allAdventurers = guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .toList();

        allAdventurers = filterAdventurersBySkill(Skill.SWORDSMANSHIP, guilds);

        System.out.println("\nADVENTURERS WITH SWORDSMANSHIP:");
        allAdventurers.forEach(System.out::println);

        System.out.println("\nWealthiest");
        // n identifies the number of adventurers we want to show per guild as their wealthiest
        Map<String, List<Adventurer>> result = identifyTopWealthiestAdventurersPerGuild(1, guilds);

        result.forEach((guildName, adventurers) -> {
            System.out.println("Guild: " + guildName);
            adventurers.forEach(System.out::println);
        });

        System.out.println("\nAverage Gold Earned By Skill SWORDSMANSHIP:");
        System.out.println(averageGoldEarned(guilds));

        System.out.println("\nFiltering adventurers by role");
        System.out.println(filterAdventurersByRole(guilds));

        System.out.println("\nIdentify most unique skills per guild: ");
        System.out.println(identifyMostVersatileSkillsPerGuild(guilds));

        System.out.println("\nAdventurers with custom format");
        System.out.println(createAListOfAdventurersWithCustomFormat(guilds));

        System.out.println("\nAdventurer with most skill");
        System.out.println(findAdventurerWithMostSkills(guilds));


        System.out.println(rankGuildsByAverageAge(guilds));

        System.out.println("\nSkill Wise Adventurer Count");
        System.out.println(skillWiseAdventurerCount(guilds));

        System.out.println("\nAdventurers after receiving 20% gold bonus:");
        List<Adventurer> bonusAdventurers = bonusGoldEvent(guilds);
        bonusAdventurers.forEach(adventurer -> {
            System.out.println(adventurer.getName() + " || " + adventurer.getGoldEarned());
        });

    }

    private static List<Guild> loadGuildData() throws IOException {
        String filePath = "/Users/berk/Documents/JAVA 2024/GameOfThrones/src/data.csv";
        Map<String, List<Adventurer>> guildMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] objects = line.split(",");
                if (objects.length == 6) {
                    String guildName = objects[0];
                    String name = objects[1];
                    int age = Integer.parseInt(objects[2]);
                    String role = objects[3];
                    double goldEarned = Double.parseDouble(objects[4]);
                    List<Skill> skills = new ArrayList<>();
                    for (String s : objects[5].split(" ")) {
                        Skill skill = Skill.valueOf(s);
                        skills.add(skill);
                    }
                    /*
                    List<Skill> skills = Arrays.stream(objects[5].split(" "))
                    .map(Skill::valueOf)
                    .collect(Collectors.toList());
                     */

                    Adventurer adventurer = new Adventurer(name, age, role, goldEarned, skills);
                   // guildMap.put(guildName, List.of(adventurer));
                    guildMap.computeIfAbsent(guildName, k -> new ArrayList<>()).add(adventurer);
                } else {
                    System.err.println("Line skipped due to incorrect format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            throw new RuntimeException(e);
        }
        return guildMap.entrySet().stream()
                .map(entry -> new Guild(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<Adventurer> filterAdventurersBySkill(Skill skill, List<Guild> guilds) {
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .filter(adventurer -> adventurer.getSkills().contains(skill))
                .sorted(Comparator.comparing(Adventurer::getName))
                .toList();
    }

    public static Map<String, List<Adventurer>> identifyTopWealthiestAdventurersPerGuild(int n, List<Guild> guilds) {
        Comparator<Adventurer> comparator = Comparator.comparing(Adventurer::getGoldEarned).reversed();

        return guilds.stream()
                .collect(Collectors.toMap(
                        Guild::getName,
                        g -> g.getAdventurers().stream()
                        .sorted(comparator)
                        .limit(n)
                        .collect(Collectors.toList())));
    }

    public static double averageGoldEarned(List<Guild> guilds) {
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .filter(adventurer -> adventurer.getSkills().contains(Skill.SWORDSMANSHIP))
                .mapToDouble(Adventurer::getGoldEarned)
                .average()
                .orElse(0.0);
    }

    public static Map<String, List<Adventurer>> filterAdventurersByRole(List<Guild> guilds) {
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .collect(Collectors.groupingBy(Adventurer::getRole));
    }

    public static Guild identifyMostVersatileSkillsPerGuild(List<Guild> guilds) {
        return guilds.stream()
                .max(Comparator.comparing(guild -> guild.getAdventurers().stream()
                                .flatMap(adventurer -> adventurer.getSkills().stream())
                                .distinct()
                                .count()))
                .orElse(null);
    }

    public static String createAListOfAdventurersWithCustomFormat(List<Guild> guilds){
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .map(adventurer -> adventurer.getName() + " (" + adventurer.getRole() + ")" +
                        " " + adventurer.getSkills())
                .collect(Collectors.joining("\n"));
    }

    public static Adventurer findAdventurerWithMostSkills(List<Guild> guilds) {
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .max(Comparator.comparingInt(adventurer -> adventurer.getSkills().size()))
                .orElse(null);
    }

    public static String rankGuildsByAverageAge(List<Guild> guilds) {
        return guilds.stream()
                .sorted(Comparator.comparing(guild -> guild.getAdventurers().stream()
                        .mapToInt(Adventurer::getAge)
                        .average()
                        .orElse(0.0)))
                .map(guild -> {
                    double avgAge = guild.getAdventurers().stream()
                            .mapToInt(Adventurer::getAge)
                            .average()
                            .orElse(0.0);
                    return String.format("Guild: " + guild.getName() + " || Average Age: " + avgAge +
                            " || Adventurers: " + guild.getAdventurers().size());
                })
                .collect(Collectors.joining("\n"));
    }

    public static Map<Skill, Long> skillWiseAdventurerCount(List<Guild> guilds) {
         return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .flatMap(adventurer -> adventurer.getSkills().stream())
                 .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
    }

    public static List<Adventurer> bonusGoldEvent(List<Guild> guilds) {
        return guilds.stream()
                .flatMap(guild -> guild.getAdventurers().stream())
                .filter(adventurer -> adventurer.getGoldEarned() < 1000)
                .peek(adventurer -> adventurer.setGoldEarned(adventurer.getGoldEarned() + adventurer.getGoldEarned() * 0.2))
                .collect(Collectors.toList());
    }
}
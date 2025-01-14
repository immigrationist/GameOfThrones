import java.util.List;

public class Adventurer implements Comparable<Adventurer> {
    private String name;
    private int age;
    private String role; // (e.g., "Warrior", "Wizard", "Rogue")
    private double goldEarned;
    private List<Skill> skills;

    public Adventurer(String name, int age, String role, double goldEarned, List<Skill> skills) {
        this.setName(name);
        this.setAge(age);
        this.setRole(role);
        this.setGoldEarned(goldEarned);
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getGoldEarned() {
        return goldEarned;
    }

    public void setGoldEarned(double goldEarned) {
        this.goldEarned = goldEarned;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String toString(){
        String info = "";
        info += "Name: " + this.getName() + "\n";
        info += "Age: " + this.getAge() + "\n";
        info += "Role: " + this.getRole() + "\n";
        info += "Gold Earned: " + this.getGoldEarned() + "\n";
        info += "Skills: " + this.getSkills() + "\n";
        return info;
    }

    @Override
    public int compareTo(Adventurer other) {
        return this.name.compareTo(String.valueOf(other.goldEarned));
    }
}

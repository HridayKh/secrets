package entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

public class Project {
    private static final Logger log = LogManager.getLogger(Project.class);

    public final int id;
    public final String slug;
    public final String name;
    public final String description;
    public final String createdAt;
    public final String updatedAt;

    public Project(int id, String slug, String name, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
    return "Project{" +
            "id=" + id +
            ", slug='" + slug +
            "', name='" + name +
            "', description='" + description +
            "', createdAt='" + createdAt +
            "', updatedAt='" + updatedAt +
            "'}";
    }

    public static Project parseProject(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String slug = rs.getString("slug");
            String name = rs.getString("name");
            String description = rs.getString("description");
            String createdAt = rs.getString("created_at");
            String updatedAt = rs.getString("updated_at");
            return new Project(id, slug, name, description, createdAt, updatedAt);
        } catch (Exception e) {
            log.catching(e);
        }
        return null;
    }
}

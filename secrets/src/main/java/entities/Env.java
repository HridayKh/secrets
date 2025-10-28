package entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;

public class Env {

    private static final Logger log = LogManager.getLogger(Env.class);

    public final int id;
    public final int projectId;
    public final String name;
    public final String createdAt;
    public final String updatedAt;

    public Env(int id, int projectId, String name, String createdAt, String updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Env parseEnv(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int projectId = rs.getInt("project_id");
            String name = rs.getString("name");
            String createdAt = rs.getString("created_at");
            String updatedAt = rs.getString("updated_at");
            return new Env(id, projectId, name, createdAt, updatedAt);
        } catch (Exception e) {
            log.catching(e);
        }
        return null;
    }
}

package entities;

import java.sql.ResultSet;

public class Project {
	
	public int id;
	public String slug;
	public String name;
	public String description;
	public String createdAt;
	public String updatedAt;

	public Project(int id, String slug, String name, String description, String createdAt, String updatedAt) {
		this.id = id;
		this.slug = slug;
		this.name = name;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	public static Project parseProject(ResultSet rs) {
		try {
			int id = rs.getInt("id");
			String slug = rs.getString("slug");
			String name = rs.getString("name");
			String description = rs.getString("description");
			String createdAt = rs.getString("created_at");
			String updatedAt = rs.getString("updated_at");
			return new Project(id,  slug,  name,  description,  createdAt,  updatedAt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

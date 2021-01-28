package KazukiDEV.WolkenNET.Sites.Get;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import KazukiDEV.WolkenNET.Content.BBCode;
import KazukiDEV.WolkenNET.Content.Permissions;
import KazukiDEV.WolkenNET.Content.mysql;
import KazukiDEV.WolkenNET.Main.App;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;

public class Beitr�ge implements Route {
	public Map<String, Object> m = new HashMap<>();

	public Beitr�ge() {
	}

	public Object handle(Request request, Response response) {
		Permissions.hasPermissions(request.cookie("session"), this.m, response);
		String cont = request.params(":cont").replaceAll("%20", " ");

		String sql = "SELECT * FROM `contributions` WHERE `sublink` = ?";
		ResultSet psql = mysql.Query(sql, cont);
		try {
			while (psql.next()) {
				if (psql.getInt("user_id") != Integer.parseInt(request.params(":user"))) {

				} else {
					String sql2 = "SELECT * FROM `users` WHERE `id` = ?";
					ResultSet psql2 = mysql.Query(sql2, request.params(":user"));
					while (psql2.next()) {
						m.put("author_name", psql2.getString("username"));
						m.put("perm", psql2.getString("permissions"));
						m.put("authority", psql2.getString("authority"));
						m.put("id", psql.getInt("id"));
						m.put("uid", psql2.getInt("id"));
					}
					String addview_sql = "UPDATE `contributions` SET `views` = `views` + 1 where `id` = ?";
					mysql.Exec(addview_sql, new StringBuilder().append(psql.getInt("id")).toString());
					if(psql.getBoolean("locked") == true) {
						if(((String)m.get("permissions")).contains("10")) {
							m.put("locked", true);
						}else {
							response.redirect("/?open=login&l=lnp");
							return null;
						}
					}else {
						m.put("locked", false);
					}
					m.put("titlebar", psql.getString("sublink"));
					m.put("bbcode_text", BBCode.bbcode(psql.getString("bbcode_text").replaceAll("\\<[^>]*>", "")));
					m.put("topicid", psql.getString("topic_id"));
					String sql_topic = "SELECT * FROM `topics` WHERE `id`=?";
					ResultSet sql_topic_rs = mysql.Query(sql_topic, psql.getString("topic_id"));
					while(sql_topic_rs.next()) {
						m.put("bc2", sql_topic_rs.getString("title"));
						m.put("bc2_link", sql_topic_rs.getString("sublink"));
						String sql_sites = "SELECT * FROM `sites` WHERE `id` = ?";
						ResultSet sql_sites_rs = mysql.Query(sql_sites, sql_topic_rs.getString("groupid"));
						sql_sites_rs.next();
						m.put("bcsite", sql_sites_rs.getString("text"));
						m.put("bclink", sql_sites_rs.getString("link"));
					}
					m.put("icon", psql.getString("user_id"));
					m.put("timestamp", psql.getString("timestamp"));
					m.put("banner", "/img/banner/wolken4.jpg");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Template template = App.cfg.getTemplate("beitrag.html");
			Writer out = new StringWriter();
			template.process(this.m, out);
			return out.toString();
		} catch (IOException | freemarker.template.TemplateException e) {
			// Redirect 404
			throw new RuntimeException(e);
		}
	}
}

package KazukiDEV.WolkenNET.Sites.API;

import java.util.HashMap;
import java.util.Map;

import KazukiDEV.WolkenNET.Content.Moderation;
import KazukiDEV.WolkenNET.Content.Permissions;
import spark.Request;
import spark.Response;
import spark.Route;

public class addFriend implements Route {
	public Map<String, Object> m = new HashMap<>();

	public addFriend() {
	}

	public Object handle(Request request, Response response) {
		if(Permissions.hasPermissions(request.cookie("session"), this.m, response) == false) {
			// TODO: JSON Response
			return null;
		}

		

		// TODO: Check if id is id

		String uID = request.queryParams("uname");
		try {
			Moderation.BanUser(uID);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		// TODO: Weiterleiten
		return "Nutzer wurde gebannt";

	}
}

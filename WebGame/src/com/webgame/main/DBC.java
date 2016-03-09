package com.webgame.main;

//������ �� ��������� �� ������ � ������ �����
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//������ �� ������ ������������
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

//��� �� URL �� ������ �� ��� �������  http://localhost:8080/webgame/db/...
//�� ����� ����� �� ������ ���� �������� � @Path(/mymethod) � �� �������� ����  http://localhost:8080/webgame/db/mymethod/
@Path("/db")

public class DBC {
	// ����� �� �������� � ������
	private String driver = "com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://localhost/webgame?useUnicode=true&characterEncoding=utf-8";
	private static String dbusername = "root";
	private static String dbpassword = "123";

	/*
	 * �������� �� ������������ ������������� ��� � ������ � ���������� �� ����
	 * ����� �� ���������/��� �������� � ������ ���� �� ��������� ���� ��
	 * ����������� ����������/, �������� �� ��� ������������ �� ��� ������ �����
	 * true/false , ��� ����������� �� ��������� ���� �� ����. ���, � ��� login
	 * �� ��������� � ������� �������� ����� ���� �� �����
	 */
	private Boolean checkUserExist(String username, String password) throws Exception {
		String query = "";
		if (password.equals("")) {
			query = "SELECT * FROM login WHERE Username ='" + username + "'";
		} else {
			query = "SELECT * FROM login WHERE Username ='" + username + "' AND Password ='" + password + "'";
		}
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			return true;
		}
		return false;
	}

	/*
	 * ������� �� �������� �� ����������, ����������� �� ����� � jquery � ��
	 * ������ ������ ������ ������ � id �� ���������� �������� ����� ���� ��
	 * �����
	 */
	private String changePassword(String passwordOld, String passwordNew, int id) throws Exception {
		String query = "UPDATE login SET Password = '" + passwordNew + "' WHERE Id = '" + id + "' AND Password = '"
				+ passwordOld + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		int upd = st.executeUpdate();
		if (upd == 0) {
			conn.close();
			return "Update error!";
		}
		conn.close();
		return "�������� � ���������!";

	}

	/*
	 * �������� �� ��������� �� ����� �� ������ ����� ������� ���� �� �����
	 */
	private Boolean checkTocken(String token, int id) throws Exception {
		String query = "SELECT Token FROM login WHERE Id ='" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (rs.next()) {
			String t1 = rs.getString("Token");
			if (t1.equals(token)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * update �� ����� ��� login ������� ���� �� ����� � 2 �������� ����������
	 * id � token
	 */
	private String updateToken(int id, String token) throws Exception {
		String query = "UPDATE login SET Token = '" + token + "' WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		st.execute();
		conn.close();
		return "Token update ok!";
	}

	/*
	 * ���������� �� ����� �� login
	 */

	private String generateToken() {
		Random r = new Random();
		String token = "";
		Integer xx = 1;
		for (int i = 0; i < 20; i++) {
			xx = r.nextInt(3) + 1;
			if (xx.equals(1)) {
				token += (char) (48 + r.nextInt(10));// ����� 1-0
			} else if (xx.equals(2)) {
				token += (char) (65 + r.nextInt(26));// ����� ������
			} else if (xx.equals(3)) {
				token += (char) (97 + r.nextInt(26));// ����� �����
			}

		}

		return token;// return User data??????????????????????????
	}

	/*
	 * ����������� �� ��� ���������� �������� �� �� �����
	 * http://localhost:8080/webgame/db/register?username=myusername&p1=password
	 * &p2=password&name=myname �������� �� �������� ���� ��������/�����������
	 * ��-����� �� �� ����� � jquery/ ???? �� �� �������� �������� � md5????
	 * ������ -
	 * http://localhost:8080/WebGame/db/register?username=myname2&password=2&
	 * name =myusername&mail=mymail
	 */
	@Path("/register")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String registerNewUser(@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("mail") String mail, @QueryParam("name") String name) throws Exception {

		if (username.equals("") || mail.equals("") || name.equals("")) {
			return "Check data fields!";
		}
		if (checkUserExist(username, "")) {
			return "User exist!";
		}
		User usr = new User();
		usr.setName(name);
		usr.setUsername(username);
		usr.setPassword(password);
		usr.setMail(mail);

		String query = "INSERT INTO `login`(`Username`, `Password`, `Nickname`, `Status`, `Mail`) VALUES ('"
				+ usr.getUsername() + "','" + usr.getPassword() + "','" + usr.getName() + "','0','" + usr.getMail()
				+ "')";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		PreparedStatement st = conn.prepareStatement(query);
		st.execute();
		conn.close();
		return "������� ����������: " + usr.getUsername() + " !";
	}

	/*
	 * ���� � ���������, ��������� �� �� ������� ���������� � ������, ��� ��
	 * ����� true �� �������� �����, ������� �� � ������ � �� ����� � ����,
	 * ����� ����� �� ���� User ������
	 * -http://localhost:8080/WebGame/db/login?username=myname2&password=m
	 */
	@Path("/login")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User webLogin(@QueryParam("username") String username, @QueryParam("password") String password)
			throws Exception {
		User usr = new User();
		if (checkUserExist(username, password)) {
			String query = "SELECT * FROM login WHERE Username = '" + username + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				usr.setId(rs.getInt("Id"));
				usr.setName(rs.getString("Nickname"));
				usr.setMail(rs.getString("Mail"));
				usr.setUsername(rs.getString("Username"));
				usr.setPassword(rs.getString("Password"));
				usr.setStatus(rs.getString("Status"));
				usr.setTeam(rs.getString("Team"));
				usr.setToken(generateToken());
			}
			updateToken(usr.getId(), usr.getToken());

		}
		return usr;
	}

	/*
	 * ������� �� ������������� ������ ������ -
	 * http://localhost:8080/WebGame/db/chpass?passwordOld=m22&passwordNew=
	 * asdasd&id=1
	 */
	@Path("/chpass")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String changeUserPassword(@QueryParam("passwordOld") String passwordOld,
			@QueryParam("passwordNew") String passwordNew, @QueryParam("id") int id) throws Exception {

		return changePassword(passwordOld, passwordNew, id);
	}

	/*
	 * ������� � ����� ������������ �� ������ �� �������� id ������ -
	 * http://localhost:8080/WebGame/db/getpt?team=�����
	 */
	@Path("/getpt")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getPoints(@QueryParam("team") String team) throws Exception {
		String result = null;
		String query = "SELECT * FROM team WHERE Name = '" + team + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> statList = new ArrayList<String>();
		if (rs.next()) {
			result = rs.getInt("Stat1") + "|" + rs.getInt("Stat2") + "|" + rs.getInt("Stat3") + "|" + rs.getInt("Stat4")
					+ "|" + rs.getInt("ExtraStat");
			statList.add(result);
		}
		return statList.toString();
	}

	/*
	 * ����� � ������ �� ������������ �� ������ ���� ���� ����������� � ��������
	 * �������, ������� �� 4-�� ����������,�������������� �����, ������ ��
	 * ��������� � id-�� �� ������ ������ :
	 * http://localhost:8080/WebGame/db/setpt?id=1&stat1=6&stat2=3&stat3=9&stat4
	 * =2&extra=11&token=00000000000
	 */
	@Path("/setpt")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String setPoints(@QueryParam("id") int id, @QueryParam("stat1") int stat1, @QueryParam("stat2") int stat2,
			@QueryParam("stat3") int stat3, @QueryParam("stat4") int stat4, @QueryParam("extra") int extra,
			@QueryParam("token") String token) throws Exception {
		if (checkTocken(token, id)) {
			String query = "UPDATE team SET Stat1='" + stat1 + "',Stat2='" + stat2 + "',Stat3='" + stat3 + "',Stat4='"
					+ stat4 + "' WHERE Id='" + id + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			conn.close();
			return "Update complete!";
		}
		return "Update failed!!";

	}

	/*
	 * ����� �� ����� �� ������, ����������� ������ ����� � Id = 0, ���� �����
	 * �� ������� �������� Id � ��������� �� ������ � Id � � ������ � �������
	 * login ������ -
	 * http://localhost:8080/WebGame/db/pickteam?uid=1&teamid=3&token=
	 * 000000000000&teamname=Barcelona
	 */

	@Path("/pickteam")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String pickTeam(@QueryParam("uid") int uid, @QueryParam("teamid") int teamid,
			@QueryParam("token") String token, @QueryParam("teamname") String team) throws Exception {
		if (checkTocken(token, uid)) {
			String query = "UPDATE team SET UserId = '" + uid + "' WHERE Id = '" + teamid + "'";
			String query2 = "UPDATE login SET Team = '" + team + "' WHERE Id = '" + uid + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			st = conn.prepareStatement(query2);
			st.execute();
			conn.close();
			return "User picked team - " + team;
		}
		return "error";
	}

	/*
	 * ������ �� ��������� �� ��������� ������ ������ -
	 * http://localhost:8080/WebGame/db/getteams
	 */

	@Path("/getteams")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAvailableTeams() throws Exception {

		String result = "";
		String query = "SELECT * FROM team WHERE UserId = '0'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> teamList = new ArrayList<String>();

		while (rs.next()) {
			result = rs.getInt("Id") + "|" + rs.getString("Name") + "|" + rs.getInt("Stat1") + "|" + rs.getInt("Stat2")
					+ "|" + rs.getInt("Stat3") + "|" + rs.getInt("Stat4");
			teamList.add(result);
		}
		conn.close();
		return teamList.toString();

	}

	/*
	 * ������� ���������� �� ������ �� �������� ������ ������, ������ �.�.
	 * ������ �� id �� ������, ����� ����� �� ���� Team � JSON ������ -
	 * http://localhost:8080/WebGame/db/teamstats?id=3
	 */

	@Path("/teamstats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Team getTeamStatistics(@QueryParam("id") int id) throws Exception {
		Team team = new Team();
		String query = "SELECT * FROM team WHERE Id = '" + id + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			team.setId(rs.getInt("Id"));
			team.setName(rs.getString("Name"));
			team.setUid(rs.getInt("UserId"));
			team.setStat1(rs.getInt("Stat1"));
			team.setStat2(rs.getInt("Stat2"));
			team.setStat3(rs.getInt("Stat3"));
			team.setStat4(rs.getInt("Stat4"));
			team.setExtra(rs.getInt("ExtraStat"));
			team.setPlayed(rs.getInt("Played"));
			team.setWons(rs.getInt("Wons"));
			team.setDraws(rs.getInt("Drws"));
			team.setLoss(rs.getInt("Loss"));
			team.setGoals(rs.getInt("Goals"));
			team.setPoints(rs.getInt("Points"));
		}
		return team;
	}

	/*
	 * ������� ������� � ��������� �� ��������
	 */
	@Path("/getStandingTable")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getStatndingTable() throws Exception {
		String result = "";
		String query = "SELECT team.*, login.Nickname AS LName FROM team LEFT JOIN login ON team.UserId = login.Id ORDER BY Points DESC ,Goals DESC, Name ASC";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		List<String> teamList = new ArrayList<String>();

		while (rs.next()) {
			result = rs.getString("Name") + "|" + rs.getInt("Played") + "|" + rs.getInt("Wons") + "|"
					+ rs.getInt("Drws") + "|" + rs.getInt("Loss") + "|" + rs.getInt("Goals") + "|" + rs.getInt("Points")
					+ "|" + rs.getString("LName");
			teamList.add(result);
		}
		conn.close();
		return teamList.toString();
	}

	/*
	 * ������ ����� ================������� ����� �� ���� User � �� ����� � JSON
	 */
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User test() {

		User usr = new User(1, "myname");
		return usr;
	}

}

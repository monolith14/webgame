package com.webgame.main;

//������ �� ��������� �� ������ � ������ �����
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
//������ �� ������ ������������
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;

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
	 * �������� �� ���������� �� ���������������� �����
	 */
	private Boolean checkAdmin(String username) throws Exception {
		String query = "SELECT Status FROM login WHERE Username ='" + username + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);

		if (rs.next()) {
			String t1 = rs.getString("Status");
			if (t1.equals(1)) {
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
	 * ���������� �� ��� �� ������ �����, private ����� �������� �� ��
	 * ���������� �� ������
	 */
	private String generatePlayerName() throws Exception {
		String name = "";
		String query1 = "SELECT Name FROM firstname ORDER BY RAND() LIMIT 1";
		String query2 = "SELECT Name FROM lastname ORDER BY RAND() LIMIT 1";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query1);
		if (rs.next()) {
			name += rs.getString("Name") + " ";
		}
		rs = st.executeQuery(query2);
		if (rs.next()) {
			name += rs.getString("Name");
		}
		conn.close();
		return name;
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
			String query = "SELECT login.*, team.Name as tTeam FROM login LEFT JOIN team ON login.Team = team.Id WHERE Username = '"
					+ username + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			String teamName = "0";
			if (rs.next()) {
				usr.setId(rs.getInt("Id"));
				usr.setName(rs.getString("Nickname"));
				usr.setMail(rs.getString("Mail"));
				usr.setUsername(rs.getString("Username"));
				usr.setPassword(rs.getString("Password"));
				usr.setStatus(rs.getString("Status"));
				usr.setTeamId(rs.getInt("Team"));
				if (rs.getString("tTeam") != null) {
					teamName = rs.getString("tTeam");
				}
				usr.setTeam(teamName);
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
			String query2 = "UPDATE login SET Team = '" + teamid + "' WHERE Id = '" + uid + "'";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
			st = conn.prepareStatement(query2);
			st.execute();
			conn.close();
			return "������ ���� - " + team;
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
			team.setMoney(rs.getInt("Money"));
		}
		return team;
	}

	/*
	 * ������� ������� � ��������� �� �������� ������
	 * http://localhost:8080/WebGame/db/getStandingTable/
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
	 * admin ����� �������� �� � �������� �� ������ �������� ����� 1-��������
	 * ��� 2-���������� �� ����� �� ����� Player 3-������� ��������� � ������
	 * ������ http://localhost:8080/WebGame/db/createplayer?qty=5
	 */
	@Path("/createplayer")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String createPlayer(@QueryParam("qty") int qty) throws ClassNotFoundException, Exception {
		String query = "";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		int tmpIntValue, tmpOAll;
		int tmpA = 0;
		int tmpD = 0;
		int tmpT = 0;
		int tmpS = 0;
		Random r = new Random();
		Player player = new Player();
		for (int i = 0; i < qty; i++) {
			player.setName(generatePlayerName());
			// ���������� �� age 19 - 33, ��� � ��� 24 ����� 4 �� �� ��
			// ��������� ��-����� ������
			tmpIntValue = 19 + r.nextInt(14);
			if (tmpIntValue > 24) {
				tmpIntValue -= 4;
			}
			player.setAge(tmpIntValue);
			// �������� �� �� ������� � ���������� �� age
			tmpOAll = tmpIntValue + 3;
			if (tmpIntValue > 20 && tmpIntValue <= 26) {
				tmpOAll += 6;
			} else if (tmpIntValue > 26) {
				tmpOAll += 4;
			}
			// ���������� �� �������
			// 0-12 ������, 13-38 ���., 39-80������, 81-99 ���.
			tmpIntValue = r.nextInt(100);
			if (tmpIntValue < 12) {
				player.setPrimePosition(1);
			} else if (tmpIntValue > 12 && tmpIntValue <= 38) {
				player.setPrimePosition(2);
			} else if (tmpIntValue > 38 && tmpIntValue <= 80) {
				player.setPrimePosition(3);
			} else if (tmpIntValue > 80) {
				player.setPrimePosition(4);
			}
			// ���������� �� ������������
			// 19-22(19-22),23-26(23-26(+6),26-33(26-33(+4)),
			switch (player.getPrimePosition()) {
			case 1:
				tmpA = (int) Math.round(tmpOAll * 0.05);
				tmpD = (int) Math.round(tmpOAll * 0.7);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.24)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 2:
				tmpA = (int) Math.round(tmpOAll * 0.1);
				tmpD = (int) Math.round(tmpOAll * 0.6);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.29)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 3:
				List<Integer> lst = new ArrayList<>();
				lst.add(1);
				lst.add(2);
				lst.add(3);
				lst.add(4);
				Collections.shuffle(lst);
				// �������� 1 �������
				switch (lst.get(0)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.5);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.5);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.5);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.5);
					break;
				}
				// �������� �� ����� �������
				switch (lst.get(1)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.35);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.35);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.35);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.35);
					break;
				}
				// �������� 3 �������
				switch (lst.get(2)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.1);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.1);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.1);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.1);
					break;
				}
				// �������� �������� ������
				switch (lst.get(3)) {
				case 1:
					tmpA = (int) Math.round(tmpOAll * 0.05);
					break;
				case 2:
					tmpD = (int) Math.round(tmpOAll * 0.05);
					break;
				case 3:
					tmpS = (int) Math.round(tmpOAll * 0.05);
					break;
				case 4:
					tmpT = (int) Math.round(tmpOAll * 0.05);
					break;
				}
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			case 4:
				tmpA = (int) Math.round(tmpOAll * 0.7);
				tmpD = (int) Math.round(tmpOAll * 0.05);
				tmpS = r.nextInt((int) Math.round(1 + (tmpOAll * 0.23)));
				tmpT = tmpOAll - (tmpA + tmpS + tmpD) + 1;
				player.setS1(tmpA);
				player.setS2(tmpD);
				player.setS3(tmpS);
				player.setS4(tmpT);
				break;
			}
			// ������ �� ������, � % �� �� ��� ��������� ��� ���������� ��
			// ������������
			player.setTallent(1 + r.nextInt(10));
			// ���� �� ������ - ���� 100 000*(������*���� �� �������)%
			switch (player.getAge()) {
			case 19:
				player.setMoney((int) (100000 * (player.getTallent() * 0.30)));
				break;
			case 20:
				player.setMoney((int) (100000 * (player.getTallent() * 0.28)));
				break;
			case 21:
				player.setMoney((int) (100000 * (player.getTallent() * 0.26)));
				break;
			case 22:
				player.setMoney((int) (100000 * (player.getTallent() * 0.24)));
				break;
			case 23:
				player.setMoney((int) (100000 * (player.getTallent() * 0.22)));
				break;
			case 24:
				player.setMoney((int) (100000 * (player.getTallent() * 0.20)));
				break;
			case 25:
				player.setMoney((int) (100000 * (player.getTallent() * 0.16)));
				break;
			case 26:
				player.setMoney((int) (100000 * (player.getTallent() * 0.12)));
				break;
			case 27:
				player.setMoney((int) (100000 * (player.getTallent() * 0.10)));
				break;
			default:
				player.setMoney((int) (100000 * (player.getTallent() * 0.08)));
				break;
			}

			// insert v bazata
			query = "INSERT INTO `players`(`Name`, `Age`, `S1`, `S2`, `S3`, `S4`, `Tallent`, `PrimePosition`, `Money`) VALUES ('"
					+ player.getName() + "','" + player.getAge() + "','" + player.getS1() + "','" + player.getS2()
					+ "','" + player.getS3() + "','" + player.getS4() + "','" + player.getTallent() + "','"
					+ player.getPrimePosition() + "','" + player.getMoney() + "')";
			PreparedStatement st = conn.prepareStatement(query);
			st.execute();
		}
		conn.close();
		return "<h4>���������� �� " + qty + " ���� ������ � ������!</h4>";

	}

	/*
	 * ����� ����� �� ������������ �� �������� � ������,
	 * ������(2)��������(5)������(7)���������(4)- 18 ������ ������
	 * http://localhost:8080/WebGame/db/distributeplayers
	 */
	@Path("/distributeplayers")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String distributePlayers() throws Exception {
		String query = "SELECT * FROM status";

		// ������ �� ��������� �� Id �� ��������
		String query1 = "SELECT Id FROM Team";
		String query2;
		String condPos = null;
		String returnlist = null;
		boolean done = false;
		int teamId, playerId, tempNum;
		// ������ � Id �� ��������
		List<Integer> teamList = new ArrayList<Integer>();
		// �������� ����, ������ �� ������� �������� �� ������������� ��� �����
		// �����(18 ������ ���������:�������2, ���5, ����7 ���4)
		List<Integer> tempList = new ArrayList<Integer>();
		// ���� ��� �������� �� ��������
		List<Integer> tempNumbersList = new ArrayList<>();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			if (rs.getInt("DistributePlayers") == 1) {
				done = true;
				returnlist = "<h3>�������� ���� �� ������������ �� ������!</h3>";
			}
		}
		if (!done) {
			st = conn.createStatement();
			rs = st.executeQuery(query1);
			PreparedStatement st2 = null;
			// ��������� �� Id �� �������� � ����
			while (rs.next()) {
				teamList.add(rs.getInt("Id"));
			}
			returnlist += teamList.toString();
			// ����������� �� ���������� �� ����� �� ��-�������� ����
			Collections.shuffle(teamList);
			// �� ����� �������(�����) � ����� �������� ���������� ������ � ��
			// �������� � ���� ����, ���� ����� ���������� ������ � Id �� ������
			for (int i = 0; i < teamList.size(); i++) {
				// �������� �� �������� �� 2 �� 42
				for (int ii = 2; ii < 42; ii++) {
					tempNumbersList.add(ii);
				}
				// ����������� �� �����
				Collections.shuffle(tempNumbersList);
				// ��� ������� ������� � < 18 � >28 �������� 1 �� 2-�� ������� �
				// ����� �� �� �� ������� ����� �� �������
				if (tempNumbersList.get(0) < 18 || tempNumbersList.get(0) > 28) {
					tempNumbersList.add(1, 1);
				}
				// ������ �� ���������� 2 ����� �� ������� ������
				query2 = "SELECT Id FROM players WHERE PrimePosition = '1' AND TeamId = '0' ORDER BY RAND() LIMIT 2";
				rs = st.executeQuery(query2);
				while (rs.next()) {
					tempList.add(rs.getInt("Id"));
				}
				// ������ �� ���������� 5 ������ �� ��������
				query2 = "SELECT Id FROM players WHERE PrimePosition = '2' AND TeamId = '0' ORDER BY RAND() LIMIT 5";
				rs = st.executeQuery(query2);
				while (rs.next()) {
					tempList.add(rs.getInt("Id"));
				}
				// ������ �� ���������� 7 ������ �� ������
				query2 = "SELECT Id FROM players WHERE PrimePosition = '3' AND TeamId = '0' ORDER BY RAND() LIMIT 7";
				rs = st.executeQuery(query2);
				while (rs.next()) {
					tempList.add(rs.getInt("Id"));
				}
				// ������ �� ���������� 4 ������ �� ���������
				query2 = "SELECT Id FROM players WHERE PrimePosition = '4' AND TeamId = '0' ORDER BY RAND() LIMIT 4";
				rs = st.executeQuery(query2);
				while (rs.next()) {
					tempList.add(rs.getInt("Id"));
				}

				returnlist += "</br>otbor " + (i + 1) + "-" + tempList.toString();
				teamId = teamList.get(i);
				// ������ �� �������� � Id �� ������, ���� �� ���������� �
				// ��������
				// �� ��������� ���� � �������, ���� �������������� ����� �
				// 4-4-2
				for (int j = 0; j < tempList.size(); j++) {
					playerId = tempList.get(j);
					if (j == 4 || j == 9 || j > 11) {
						switch (j) {
						case 4:
						case 9:
						case 12:
						case 13:
						case 14:
						case 17:
							condPos = "0";
							break;
						case 15:
							condPos = "13";
							break;
						case 16:
							condPos = "15";
							break;
						}
					} else {
						condPos = Integer.toString(j);
					}
					tempNum = tempNumbersList.get(j);
					query2 = "UPDATE players SET TeamId = '" + teamId + "',Position ='" + condPos + "', PlayNumber ='"
							+ tempNum + "' WHERE Id='" + playerId + "'";
					st2 = conn.prepareStatement(query2);
					st2.execute();
				}
				tempList.clear();
			}
			query = "UPDATE status SET DistributePlayers = '1'";
			PreparedStatement st4 = conn.prepareStatement(query);
			st4.execute();
			conn.close();
		}

		return returnlist;
	}

	/*
	 * ����� ����� �� ���� Player � JSON ������ �� �������� Id ������
	 * http://localhost:8080/WebGame/db/getplayer?id=1
	 */
	@Path("/getplayer")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Player getPlayerById(@QueryParam("id") int id) throws Exception {
		String query = "SELECT * FROM players WHERE Id='" + id + "'";
		Player pl = new Player();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			pl.setId(rs.getInt("Id"));
			pl.setName(rs.getString("Name"));
			pl.setAge(rs.getInt("Age"));
			pl.setS1(rs.getInt("S1"));
			pl.setS2(rs.getInt("S2"));
			pl.setS3(rs.getInt("S3"));
			pl.setS4(rs.getInt("S4"));
			pl.setTallent(rs.getInt("Tallent"));
			pl.setTeamId(rs.getInt("TeamId"));
			pl.setPosition(rs.getInt("Position"));
			pl.setCondition(rs.getInt("Condition"));
			pl.setPrimePosition(rs.getInt("PrimePosition"));
			pl.setMoney(rs.getInt("Money"));
			pl.setPlayNumber(rs.getInt("PlayNumber"));
		}
		conn.close();
		return pl;
	}

	/*
	 * ��������� �� ������, ������� ����� �� ���� Playstyle � �� ����� � JSON
	 * ������ http://localhost:8080/WebGame/db/groupteam?teamid=4
	 */
	@Path("/groupteam")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Playstyle groupTeam(@QueryParam("teamid") int teamid) throws Exception {
		String query = "SELECT Id,Position FROM players WHERE TeamId = '" + teamid + "'";
		List<Integer> teamListR = new ArrayList<>();
		int df = 0;
		int md = 0;
		int fw = 0;
		Playstyle pl = new Playstyle();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			switch (rs.getInt("Position")) {
			case 0:
				teamListR.add(rs.getInt("Id"));
				break;
			case 1:
				pl.setGk(getPlayerById(rs.getInt("Id")));
				break;
			case 2:
				pl.setDf1(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 3:
				pl.setDf2(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 4:
				pl.setDf3(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 5:
				pl.setDf4(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 6:
				pl.setDf5(getPlayerById(rs.getInt("Id")));
				df++;
				break;
			case 7:
				pl.setMd1(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 8:
				pl.setMd2(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 9:
				pl.setMd3(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 10:
				pl.setMd4(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 11:
				pl.setMd5(getPlayerById(rs.getInt("Id")));
				md++;
				break;
			case 12:
				pl.setFw1(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 13:
				pl.setFw2(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 14:
				pl.setFw3(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 15:
				pl.setFw4(getPlayerById(rs.getInt("Id")));
				fw++;
				break;
			case 16:
				pl.setFw5(getPlayerById(rs.getInt("Id")));
				fw++;
				break;

			}

			// teamList.add(rs.getInt("Id"));
		}
		pl.setR1(getPlayerById(teamListR.get(0)));
		pl.setR2(getPlayerById(teamListR.get(1)));
		pl.setR3(getPlayerById(teamListR.get(2)));
		pl.setR4(getPlayerById(teamListR.get(3)));
		pl.setR5(getPlayerById(teamListR.get(4)));
		pl.setR6(getPlayerById(teamListR.get(5)));
		pl.setR7(getPlayerById(teamListR.get(6)));
		pl.setDf(df);
		pl.setMd(md);
		pl.setFw(fw);
		conn.close();
		return pl;
	}

	/*
	 * ���������� �� �������� �� ������� �����-�����-����� � ���������
	 * ���������, �������� �� ���������� ��� ��������� �� ������ ������:
	 * http://localhost:8080/WebGame/db/createrounds
	 */
	@Path("/createrounds")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String createRounds() throws Exception {
		String result = "", query = "SELECT * FROM status", reverse = "";
		String[] s1, s2, s3, s4;
		String tVal = null;
		int i = 0, j = 0, kr = 1, l = 0;
		boolean done = false;
		List<String> allPairList = new ArrayList<String>();
		List<String> roundsList = new ArrayList<String>();
		List<String> singleRoundList = new ArrayList<String>();
		List<String> roundListShuffle = new ArrayList<String>();
		List<String> tList = new ArrayList<String>();// za otborite ot bazata
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			if (rs.getInt("CreateProgram") == 1) {
				done = true;
				result = "<h3>���������� �� �������� ���� � ���������!</h3>";
			}
		}

		if (!done) {
			query = "SELECT * FROM team";
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				tList.add(rs.getString("Name"));
			}

			for (i = 0; i < tList.size(); i++) {
				for (j = i; j < tList.size(); j++) {
					if (i != j) {
						allPairList.add(tList.get(i) + ":" + tList.get(j));
					}
				}

			}

			while (!allPairList.isEmpty()) {
				singleRoundList.add(allPairList.get(0));

				for (String elm : allPairList) {
					s1 = elm.split(":");
					for (String elm2 : singleRoundList) {
						s2 = elm2.split(":");
						if (s1[0].equals(s2[0]) || s1[1].equals(s2[1]) || s1[0].equals(s2[1]) || s1[1].equals(s2[0])) {
							tVal = "";
							break;
						} else {
							tVal = elm;
						}

					}
					if (!tVal.equals("")) {
						singleRoundList.add(tVal);
					}
				}
				for (String elmnt : singleRoundList) {
					allPairList.remove(elmnt);
				}

				roundsList.add(singleRoundList.toString());
				singleRoundList.clear();

			}
			Collections.shuffle(roundsList);
			for (String sfl : roundsList) {
				sfl = sfl.replace("[", "");
				sfl = sfl.replace("]", "");
				if (l == 1) {
					s3 = sfl.split(",");
					for (i = 0; i < s3.length; i++) {
						s4 = s3[i].split(":");
						reverse = s4[1] + ":" + s4[0];
						singleRoundList.add(reverse);
					}
					roundListShuffle.add(singleRoundList.toString());
					singleRoundList.clear();
					l--;
				} else {
					roundListShuffle.add(sfl);
					l++;
				}
			}
			Collections.shuffle(roundListShuffle);

			for (String lst : roundListShuffle) {
				result += "============== ���� " + kr + " =====================</br>";
				lst = lst.replace("[", "");
				lst = lst.replace("]", "");
				s1 = lst.split(",");
				for (i = 0; i < s1.length; i++) {
					s2 = s1[i].split(":");
					result += s1[i] + "</br>";
					query = "INSERT INTO game (GameRound,Team1,Team2) VALUES ('" + kr + "','" + s2[0].trim() + "','"
							+ s2[1].trim() + "')";
					PreparedStatement st2 = conn.prepareStatement(query);
					st2.execute();
				}
				kr++;
			}
			for (String lst : roundListShuffle) {
				result += "============== ���� " + kr + " =====================</br>";
				lst = lst.replace("[", "");
				lst = lst.replace("]", "");
				s1 = lst.split(",");
				for (i = 0; i < s1.length; i++) {
					s2 = s1[i].split(":");
					result += s1[i] + "</br>";
					query = "INSERT INTO game (GameRound,Team1,Team2) VALUES ('" + kr + "','" + s2[1].trim() + "','"
							+ s2[0].trim() + "')";
					PreparedStatement st3 = conn.prepareStatement(query);
					st3.execute();
				}
				kr++;
			}

			query = "UPDATE status SET CreateProgram = '1'";
			PreparedStatement st4 = conn.prepareStatement(query);
			st4.execute();
			conn.close();
		}
		return result;
	}

	/*
	 * 
	 */

	public Playstyle groupTeamForGame(int teamid) throws Exception {
		String query = "SELECT * FROM players WHERE TeamId = '" + teamid + "'";
		List<Integer> teamListR = new ArrayList<>();
		int df = 0;
		int md = 0;
		int fw = 0;
		ArrayList<String> dfList = new ArrayList<String>();
		ArrayList<String> mdList = new ArrayList<String>();
		ArrayList<String> fwList = new ArrayList<String>();
		Integer attack = 0, defence = 0, speed = 0, technic = 0, condition = 0;
		Playstyle pl = new Playstyle();
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			switch (rs.getInt("Position")) {
			case 0:
				teamListR.add(rs.getInt("Id"));
				break;
			case 1:
				pl.setGk(getPlayerById(rs.getInt("Id")));
				attack += pl.getGk().getS1();
				defence += pl.getGk().getS2();
				speed += pl.getGk().getS3();
				technic += pl.getGk().getS4();
				condition += pl.getGk().getCondition();
				break;
			case 2:
				pl.setDf1(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf1().getS1();
				defence += pl.getDf1().getS2();
				speed += pl.getDf1().getS3();
				technic += pl.getDf1().getS4();
				condition += pl.getDf1().getCondition();
				dfList.add("df1");
				break;
			case 3:
				pl.setDf2(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf2().getS1();
				defence += pl.getDf2().getS2();
				speed += pl.getDf2().getS3();
				technic += pl.getDf2().getS4();
				condition += pl.getDf2().getCondition();
				dfList.add("df2");
				break;
			case 4:
				pl.setDf3(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf3().getS1();
				defence += pl.getDf3().getS2();
				speed += pl.getDf3().getS3();
				technic += pl.getDf3().getS4();
				condition += pl.getDf3().getCondition();
				dfList.add("df3");
				break;
			case 5:
				pl.setDf4(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf4().getS1();
				defence += pl.getDf4().getS2();
				speed += pl.getDf4().getS3();
				technic += pl.getDf4().getS4();
				condition += pl.getDf4().getCondition();
				dfList.add("df4");
				break;
			case 6:
				pl.setDf5(getPlayerById(rs.getInt("Id")));
				df++;
				attack += pl.getDf5().getS1();
				defence += pl.getDf5().getS2();
				speed += pl.getDf5().getS3();
				technic += pl.getDf5().getS4();
				condition += pl.getDf5().getCondition();
				dfList.add("df5");
				break;
			case 7:
				pl.setMd1(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd1().getS1();
				defence += pl.getMd1().getS2();
				speed += pl.getMd1().getS3();
				technic += pl.getMd1().getS4();
				condition += pl.getMd1().getCondition();
				mdList.add("md1");
				break;
			case 8:
				pl.setMd2(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd2().getS1();
				defence += pl.getMd2().getS2();
				speed += pl.getMd2().getS3();
				technic += pl.getMd2().getS4();
				condition += pl.getMd2().getCondition();
				mdList.add("md2");
				break;
			case 9:
				pl.setMd3(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd3().getS1();
				defence += pl.getMd3().getS2();
				speed += pl.getMd3().getS3();
				technic += pl.getMd3().getS4();
				condition += pl.getMd3().getCondition();
				mdList.add("md3");
				break;
			case 10:
				pl.setMd4(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd4().getS1();
				defence += pl.getMd4().getS2();
				speed += pl.getMd4().getS3();
				technic += pl.getMd4().getS4();
				condition += pl.getMd4().getCondition();
				mdList.add("md4");
				break;
			case 11:
				pl.setMd5(getPlayerById(rs.getInt("Id")));
				md++;
				attack += pl.getMd5().getS1();
				defence += pl.getMd5().getS2();
				speed += pl.getMd5().getS3();
				technic += pl.getMd5().getS4();
				condition += pl.getMd5().getCondition();
				mdList.add("md5");
				break;
			case 12:
				pl.setFw1(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw1().getS1();
				defence += pl.getFw1().getS2();
				speed += pl.getFw1().getS3();
				technic += pl.getFw1().getS4();
				condition += pl.getFw1().getCondition();
				fwList.add("fw1");
				break;
			case 13:
				pl.setFw2(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw2().getS1();
				defence += pl.getFw2().getS2();
				speed += pl.getFw2().getS3();
				technic += pl.getFw2().getS4();
				condition += pl.getFw2().getCondition();
				fwList.add("fw2");
				break;
			case 14:
				pl.setFw3(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw3().getS1();
				defence += pl.getFw3().getS2();
				speed += pl.getFw3().getS3();
				technic += pl.getFw3().getS4();
				condition += pl.getFw3().getCondition();
				fwList.add("fw3");
				break;
			case 15:
				pl.setFw4(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw4().getS1();
				defence += pl.getFw4().getS2();
				speed += pl.getFw4().getS3();
				technic += pl.getFw4().getS4();
				condition += pl.getFw4().getCondition();
				fwList.add("fw4");
				break;
			case 16:
				pl.setFw5(getPlayerById(rs.getInt("Id")));
				fw++;
				attack += pl.getFw5().getS1();
				defence += pl.getFw5().getS2();
				speed += pl.getFw5().getS3();
				technic += pl.getFw5().getS4();
				condition += pl.getFw5().getCondition();
				fwList.add("fw5");
				break;

			}

			// teamList.add(rs.getInt("Id"));
		}
		pl.setR1(getPlayerById(teamListR.get(0)));
		pl.setR2(getPlayerById(teamListR.get(1)));
		pl.setR3(getPlayerById(teamListR.get(2)));
		pl.setR4(getPlayerById(teamListR.get(3)));
		pl.setR5(getPlayerById(teamListR.get(4)));
		pl.setR6(getPlayerById(teamListR.get(5)));
		pl.setR7(getPlayerById(teamListR.get(6)));
		pl.setDf(df);
		pl.setMd(md);
		pl.setFw(fw);
		pl.setDfList(dfList);
		pl.setMdList(mdList);
		pl.setFwList(fwList);
		query = "SELECT Name FROM team WHERE Id = '" + teamid + "'";
		st = conn.createStatement();
		rs = st.executeQuery(query);
		if (rs.next()) {
			pl.setName(rs.getString("Name"));
		}
		pl.setAttack(attack);
		pl.setDefence(defence);
		pl.setSpeed(speed);
		pl.setTechnic(technic);
		pl.setCondition(condition);
		conn.close();
		return pl;
	}

	/*
	 * ������ ����� �� ���
	 */
	@Path("/playgame")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String playGame() throws Exception {
		Random r = new Random();
		Status status = new Status();
		String query = "", result = "", comment="";
		Integer tVal1, tVal2, attackDirection, ballPosition, flang, tVal3, tVal4, tVal5, tVal6, gA = 0, gB = 0, a = 0,
				b = 0;
		ArrayList<Integer> passOk = new ArrayList(Arrays.asList(11,51,11,121,31,11,51,11,31,11,11,31,11,121));
		ArrayList<Integer> passFail = new ArrayList(Arrays.asList(12,12,41,12,41,12,41,41,41,12,41,121));
		ArrayList<Integer> goal = new ArrayList(Arrays.asList(21));
		ArrayList<Integer> goalFail = new ArrayList(Arrays.asList(22));
		Integer checker;
		Game game = new Game();
		query = "SELECT CurrentRound FROM status";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st2;
		PreparedStatement st3;
		ResultSet rs2, rs3;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			status.setRound(rs.getInt(1));
		}
		result += "<h3>��������� �� " + status.getRound().toString() + " ����</h3></br>";
		query = "SELECT * FROM game WHERE GameRound = '" + status.getRound() + "'";
		st = conn.createStatement();
		rs = st.executeQuery(query);
		while (rs.next()) {
			comment = "";
			tVal1 = 0;
			tVal2 = 0;
			attackDirection = 0;
			checker = 0;
			ballPosition = 0;
			flang = 0;
			tVal3 = 0;
			tVal4 = 0;
			tVal5 = 0;
			tVal6 = 0;
			gA = 0;
			gB = 0;
			query = "SELECT * FROM team WHERE Name ='" + rs.getString("Team1") + "'";
			st2 = conn.createStatement();
			rs2 = st2.executeQuery(query);
			if (rs2.next()) {
				game.setTeamA(groupTeamForGame(rs2.getInt("Id")));// id na
																	// otbora
			}
			query = "SELECT * FROM team WHERE Name ='" + rs.getString("Team2") + "'";
			st2 = conn.createStatement();
			rs2 = st2.executeQuery(query);
			if (rs2.next()) {
				game.setTeamB(groupTeamForGame(rs2.getInt("Id")));// id na
																	// otbora
			}

			// ��� ���������� �� ������/���� �� 4 ���������� �� ������ ������
			tVal1 = game.getTeamA().getAttack() + game.getTeamA().getDefence() + game.getTeamA().getSpeed()
					+ game.getTeamA().getTechnic();
			tVal2 = game.getTeamB().getAttack() + game.getTeamB().getDefence() + game.getTeamB().getSpeed()
					+ game.getTeamB().getTechnic();

			ballPosition = 2;
			flang = 2;
			attackDirection = r.nextInt(2) + 1;
			for (Integer i = 0; i < 90; i++) {
				switch (attackDirection) {
				case 1:
					switch (ballPosition) {
					case 1:
						checker = dfVsFw(game.getTeamA(), game.getTeamB());
						if (checker > 50) {
							ballPosition = 2;
							attackDirection = 1;
							comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passOk.get(r.nextInt(passOk.size())) )+"|";
						} else {
							ballPosition = 1;
							attackDirection = 2;
							comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passFail.get(r.nextInt(passFail.size())) )+"|";
						}
						break;
					case 2:
						checker = mdfVsMd(game.getTeamA(), game.getTeamB());
						if (checker > 50) {
							ballPosition = 3;
							attackDirection = 1;
							comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passOk.get(r.nextInt(passOk.size())) )+"|";
						} else {
							ballPosition = 2;
							attackDirection = 2;
							comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passFail.get(r.nextInt(passFail.size())) )+"|";
						}
						break;
					case 3:
						checker = dfVsFw(game.getTeamA(), game.getTeamB());
						if (checker < 50) {
							ballPosition = 3;
							if (r.nextInt(100) > 90) {
								gA++;
								comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,goal.get(r.nextInt(goal.size())) )+"|";
							}
							else{
								comment+= "t1+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,goalFail.get(r.nextInt(goalFail.size())) )+"|";
							}
							attackDirection = 2;
						}
						break;
					}// krai na switch ballPosition
					break;
				case 2:
					switch (ballPosition) {
					case 1:
						checker = dfVsFw(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 1;
							if (r.nextInt(100) > 90) {
								gB++;
								comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,goal.get(r.nextInt(goal.size())) )+"|";
							}
							else{
								comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,goalFail.get(r.nextInt(goalFail.size())) )+"|";
							}
							attackDirection = 1;
						}
						break;
					case 2:
						checker = mdfVsMd(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 1;
							attackDirection = 2;
							comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passOk.get(r.nextInt(passOk.size())) )+"|";
						} else {
							ballPosition = 2;
							attackDirection = 1;
							comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passFail.get(r.nextInt(passFail.size())) )+"|";
						}
						break;
					case 3:
						checker = dfVsFw(game.getTeamB(), game.getTeamA());
						if (checker < 50) {
							ballPosition = 2;
							attackDirection = 2;
							comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passOk.get(r.nextInt(passOk.size())) )+"|";
						} else {
							ballPosition = 3;
							attackDirection = 1;
							comment+= "t2+"+i.toString()+":"+ eventMaker(game, attackDirection, ballPosition,passFail.get(r.nextInt(passFail.size())) )+"|";

						}
						break;
					}

					break;
				}// krai na attdirection
			} // krai na cikyl 180
			result += game.getTeamA().getName() + " " + gA.toString() + " : " + gB.toString() + " "
					+ game.getTeamB().getName() + "</br>";
			// ������ �� ��������� � �������
			query = "UPDATE game SET Results = '" + gA.toString() + ":" + gB.toString() + "', Comment = '"+comment+"' WHERE Team1 ='"
					+ game.getTeamA().getName() + "' AND Team2 = '" + game.getTeamB().getName() + "'";
			st3 = conn.prepareStatement(query);
			st3.execute();
			// update na team za klasiraneto
			if (gA > gB) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Wons") + 1;
					tVal3 = rs2.getInt("Points") + 3;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Wons = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Loss") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Loss = '" + tVal2.toString()
							+ "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}
			if (gA < gB) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Wons") + 1;
					tVal3 = rs2.getInt("Points") + 3;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Wons = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Loss") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Loss = '" + tVal2.toString()
							+ "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}
			if (gA.equals(gB)) {
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamB().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Drws") + 1;
					tVal3 = rs2.getInt("Points") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Drws = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamB().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}
				query = "SELECT * FROM team WHERE Name = '" + game.getTeamA().getName() + "'";
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(query);
				if (rs2.next()) {
					tVal1 = rs2.getInt("Played") + 1;
					tVal2 = rs2.getInt("Drws") + 1;
					tVal3 = rs2.getInt("Points") + 1;
					query = "UPDATE team SET Played = '" + tVal1.toString() + "', Drws = '" + tVal2.toString()
							+ "', Points = '" + tVal3.toString() + "' WHERE Name = '" + game.getTeamA().getName() + "'";
					st3 = conn.prepareStatement(query);
					st3.execute();
				}

			}

		}
		tVal1 = status.getRound() + 1;
		query = "UPDATE status SET CurrentRound = '" + tVal1.toString() + "'";
		st3 = conn.prepareStatement(query);
		st3.execute();

		return result;
	}

	private String getComment(String playerName,String teamName, int event, int type) throws Exception {
		String result = "";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		String query = "SELECT Message FROM commentator WHERE Event = '" + event + "' AND Type = '" + type
				+ "' ORDER BY RAND() LIMIT 1";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		if (rs.next()) {
			result = rs.getString(1).replaceAll("Player", playerName);
			result = result.replaceAll("Team", teamName);
		}
		conn.close();

		return result;
	}
	
	/*�������, ���������� �� ����� ����� ������:
		1-��������
		2-���� ��� �������->(���, ���, ������)
			21-���
			22 ���
		3-������
		 31- ������ ������ -> ��� ��� ...
		 32 - �����  
		4-������� ��� ��
		5-���������
		6-�����
		7-���
		8-������
		9-���� ������
		10-������ ������
		11-�����
		12-�������� ������� �� �����������*/

	private String eventMaker(Game game, int attackDirection, int ballPosition, int eventType) throws Exception{
		String result = "";
		String teamName;
		if(attackDirection == 1){
			teamName = game.getTeamA().getName();
		}
		else{
			teamName = game.getTeamB().getName();
		}
		switch(eventType){
		case 11:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 1, 1);
			break;
		case 12:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 1, 2);
			break;
		case 21:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 2, 1);
			break;
		case 22:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 2, 2);
			break;
		case 31:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 3, 1);
			break;
		case 32:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 3, 2);
			break;
		case 51:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 5, 1);
			break;
		case 41:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 4, 1);
			break;
		case 121:
			result = getComment(getPlayerNameForComment(game, attackDirection,ballPosition),teamName, 12, 1);
			break;
		}
		
		return result;
	}
	
	
	private String getPlayerNameForComment(Game game,int attackDirection, int ballPosition){
		String result = "";
		int tempInt;
		Random r = new Random();
		switch(attackDirection){
		case 1:
			switch(ballPosition){
			case 0:
				result = game.getTeamA().getGk().getName();
				break;
			case 1:
				tempInt = r.nextInt(game.getTeamA().getDfList().size());
				switch(game.getTeamA().getDfList().get(tempInt)){
				case "df1":
					result = game.getTeamA().getDf1().getName();
					break;
				case "df2":
					result = game.getTeamA().getDf2().getName();
					break;
				case "df3":
					result = game.getTeamA().getDf3().getName();
					break;
				case "df4":
					result = game.getTeamA().getDf4().getName();
					break;
				case "df5":
					result = game.getTeamA().getDf5().getName();
					break;
				}
				break;
			case 2:
				tempInt = r.nextInt(game.getTeamA().getMdList().size());
				switch(game.getTeamA().getMdList().get(tempInt)){
				case "md1":
					result = game.getTeamA().getMd1().getName();
					break;
				case "md2":
					result = game.getTeamA().getMd2().getName();
					break;
				case "md3":
					result = game.getTeamA().getMd3().getName();
					break;
				case "md4":
					result = game.getTeamA().getMd4().getName();
					break;
				case "md5":
					result = game.getTeamA().getMd5().getName();
					break;
				}
				break;
			case 3:
				tempInt = r.nextInt(game.getTeamA().getFwList().size());
				switch(game.getTeamA().getFwList().get(tempInt)){
				case "fw1":
					result = game.getTeamA().getFw1().getName();
					break;
				case "fw2":
					result = game.getTeamA().getFw2().getName();
					break;
				case "fw3":
					result = game.getTeamA().getFw3().getName();
					break;
				case "fw4":
					result = game.getTeamA().getFw4().getName();
					break;
				case "fw5":
					result = game.getTeamA().getFw5().getName();
					break;
				}
				break;
			}
			//krai na line
			break;
			
			
			//teamB
		case 2:
			switch(ballPosition){
			case 0:
				result = game.getTeamB().getGk().getName();
				break;
			case 1:
				tempInt = r.nextInt(game.getTeamB().getDfList().size());
				switch(game.getTeamB().getDfList().get(tempInt)){
				case "df1":
					result = game.getTeamB().getDf1().getName();
					break;
				case "df2":
					result = game.getTeamB().getDf2().getName();
					break;
				case "df3":
					result = game.getTeamB().getDf3().getName();
					break;
				case "df4":
					result = game.getTeamB().getDf4().getName();
					break;
				case "df5":
					result = game.getTeamB().getDf5().getName();
					break;
				}
				break;
			case 2:
				tempInt = r.nextInt(game.getTeamB().getMdList().size());
				switch(game.getTeamB().getMdList().get(tempInt)){
				case "md1":
					result = game.getTeamB().getMd1().getName();
					break;
				case "md2":
					result = game.getTeamB().getMd2().getName();
					break;
				case "md3":
					result = game.getTeamB().getMd3().getName();
					break;
				case "md4":
					result = game.getTeamB().getMd4().getName();
					break;
				case "md5":
					result = game.getTeamB().getMd5().getName();
					break;
				}
				break;
			case 3:
				tempInt = r.nextInt(game.getTeamB().getFwList().size());
				switch(game.getTeamB().getFwList().get(tempInt)){
				case "fw1":
					result = game.getTeamB().getFw1().getName();
					break;
				case "fw2":
					result = game.getTeamB().getFw2().getName();
					break;
				case "fw3":
					result = game.getTeamB().getFw3().getName();
					break;
				case "fw4":
					result = game.getTeamB().getFw4().getName();
					break;
				case "fw5":
					result = game.getTeamB().getFw5().getName();
					break;
				}
				break;
			}
			//krai na line
			break;
		}
		
		return result;
	}

	private int dfVsFw(Playstyle plA, Playstyle plB) {
		Integer tVal1, tVal2, tVal3, tVal4;
		Double d;
		Random r = new Random();
		tVal1 = 1;
		tVal2 = 1;
		tVal3 = 1;
		tVal4 = 1;
		if (plA.getDf1() != null) {
			tVal1 = +plA.getDf1().getS2() + plA.getDf1().getS3();
		}
		if (plA.getDf2() != null) {
			tVal1 = +plA.getDf2().getS2() + plA.getDf2().getS3();
		}
		if (plA.getDf3() != null) {
			tVal1 = +plA.getDf3().getS2() + plA.getDf3().getS3();
		}
		if (plA.getDf4() != null) {
			tVal1 = +plA.getDf4().getS2() + plA.getDf4().getS3();
		}
		if (plA.getDf5() != null) {
			tVal1 = +plA.getDf5().getS2() + plA.getDf5().getS3();
		}
		if (plB.getFw1() != null) {
			tVal2 = +plB.getFw1().getS1() + plB.getFw1().getS3();
		}
		if (plB.getFw2() != null) {
			tVal2 = +plB.getFw2().getS1() + plB.getFw2().getS3();
		}
		if (plB.getFw3() != null) {
			tVal2 = +plB.getFw3().getS1() + plB.getFw3().getS3();
		}
		if (plB.getFw4() != null) {
			tVal2 = +plB.getFw4().getS1() + plB.getFw4().getS3();
		}
		if (plB.getFw5() != null) {
			tVal2 = +plB.getFw5().getS1() + plB.getFw5().getS3();
		}
		d = tVal1 / (double) tVal2;
		return r.nextInt((int) Math.ceil(d * 100));

	}

	private int mdfVsMd(Playstyle plA, Playstyle plB) {
		Integer tVal1, tVal2, tVal3, tVal4;
		Double d;
		Random r = new Random();
		tVal1 = 1;
		tVal2 = 1;
		tVal3 = 1;
		tVal4 = 1;
		if (plA.getMd1() != null) {
			tVal1 = +plA.getMd1().getS1() + plA.getMd1().getS3() + plA.getMd1().getS4();
		}
		if (plA.getMd2() != null) {
			tVal1 = +plA.getMd2().getS1() + plA.getMd2().getS3() + plA.getMd2().getS4();
		}
		if (plA.getMd3() != null) {
			tVal1 = +plA.getMd3().getS1() + plA.getMd3().getS3() + plA.getMd3().getS4();
		}

		if (plA.getMd4() != null) {
			tVal1 = +plA.getMd4().getS1() + plA.getMd4().getS3() + plA.getMd4().getS4();
		}
		if (plA.getMd5() != null) {
			tVal1 = +plA.getMd5().getS1() + plA.getMd5().getS3() + plA.getMd5().getS4();
		}

		if (plB.getMd1() != null) {
			tVal2 = +plB.getMd1().getS1() + plB.getMd1().getS3() + plB.getMd1().getS4();
		}
		if (plB.getMd2() != null) {
			tVal2 = +plB.getMd2().getS1() + plB.getMd2().getS3() + plB.getMd2().getS4();
		}
		if (plB.getMd3() != null) {
			tVal2 = +plB.getMd3().getS1() + plB.getMd3().getS3() + plB.getMd3().getS4();
		}
		if (plB.getMd4() != null) {
			tVal2 = +plB.getMd4().getS1() + plB.getMd4().getS3() + plB.getMd4().getS4();
		}
		if (plB.getMd5() != null) {
			tVal2 = +plB.getMd5().getS1() + plB.getMd5().getS3() + plB.getMd5().getS4();
		}

		d = tVal1 / (double) tVal2;
		return r.nextInt((int) Math.ceil(d * 100));
	}

	/*
	 * ����� ����������� �� ������ �� �����������, ������ -
	 * http://localhost:8080/WebGame/db/getmyresults?team=�������
	 */
	@Path("/getmyresults")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getMyResults(@QueryParam("team") String team) throws Exception {
		String result = "<h3>��������� �� ���������� �����:</h3></br>", checkResult = "", tVal = "";
		String query = "SELECT * FROM game WHERE Team1 = '" + team + "' OR Team2 = '" + team + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			checkResult = rs.getString("Results");
			if (checkResult.equals("0")) {
				tVal = "-:-";
			} else {
				tVal = checkResult;
			}
			result += rs.getString("Team1") + " " + tVal + " " + rs.getString("Team2") + "</br>";

		}
		return result;
	}

	/*
	 * ����� ����������� �� ������ �� �� ��������� ����, ������ -
	 * http://localhost:8080/WebGame/db/getallresults?round=1
	 */
	@Path("/getallresults")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getAllResults(@QueryParam("round") String round) throws Exception {
		String result = "<h3>��������� �� ���������� ����� �� " + round + " ����:</h3></br>", checkResult = "",
				tVal = "";
		String query = "SELECT * FROM game WHERE GameRound = '" + round + "'";
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, dbusername, dbpassword);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(query);
		while (rs.next()) {
			checkResult = rs.getString("Results");
			if (checkResult.equals("0")) {
				tVal = "-:-";
			} else {
				tVal = checkResult;
			}
			result += rs.getString("Team1") + " " + tVal + " " + rs.getString("Team2") + "</br>";

		}
		return result;
	}

	/*
	 * ������ ����� ================
	 */
	@Path("/test")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String test() {
		Player pl = new Player(1, "Novo ime ooD");
		Playstyle plstl = new Playstyle();
		plstl.setGk(pl);
		plstl.setDf1(pl);
		return "{1:���� ��� �������,2:������ �����}";
	}
}

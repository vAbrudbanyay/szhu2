package szamlazzhuprobafeladat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryMng {

  private DbCon dbc = new DbCon();
  PreparedStatement statement;
	
public void create (String vezNev, String kerNev, String email, String jelszo, String hirlevel) {
  String sql = "insert into `felhasznalok` (veznev, kernev, email, jelszo, hirlevel) values (?, ?, ?, ?, ?)";
  Connection con = dbc.getConnection();
		
    try {
  	statement = con.prepareStatement(sql);
	statement.setString(1, vezNev);
	statement.setString(2, kerNev);
	statement.setString(3, email);
	statement.setString(4, jelszo);
	statement.setString(5, hirlevel);
	statement.execute();
    } catch (SQLException e) {
	e.printStackTrace();
    } finally {
        dbc.closeConnection(con);
    }
		
}

public String szamlakiallit(String email, String[]termeknev, String[] ar, int vegosszeg, int szamlaId, int id) {
    String sql = "select * from `felhasznalok` where id=?";
    String name ="";
    String elkeszitettSzamla = null;
    Connection con = dbc.getConnection();

    try {
	statement = con.prepareStatement(sql);
	statement.setInt(1, id);
	
        ResultSet result = statement.executeQuery();
            if (result.next()) {
		name = result.getString("veznev") + " " + result.getString("kernev");
            }
			
            if(!name.equals("") && szamlaId>0) {
		try {
	            // StringBuilderrel elkészítünk egy html fájlt
                	StringBuilder htmlStringBuilder=new StringBuilder();
                        htmlStringBuilder.append("<html><head><title>Kiállított számla</title><header><h1>Név: "+name+"</h1></header></head>");
			htmlStringBuilder.append("<body>");
        
                    // létrehozunk egy táblázatot a számla információknak
                        htmlStringBuilder.append("<table border=\"1\" bordercolor=\"#CCCCCC\">");
                        htmlStringBuilder.append("<tr><td><b>Kiállított számla azonosító: "+szamlaId+"</b></td></tr>");
                        htmlStringBuilder.append("<tr><td></td></tr>");
                        htmlStringBuilder.append("</table>");

                    // létrehozunk egy táblázatot a számla tételeknek
                        htmlStringBuilder.append("<table border=\"1\" bordercolor=\"#000000\">");
		        htmlStringBuilder.append("<tr><td><b>Tétel</b></td><td><b>Termék név</b></td><td><b>Összeg</b></td></tr>");
			            
		    // kitöltöm a táblázatot a termék nevével és az árral
		        for(int i=0; i<termeknev.length; i++) {
			    htmlStringBuilder.append("<tr><td>"+(i+1)+"</td><td>"+termeknev[i]+"</td><td>"+ar[i]+" HUF </td></tr>");
                        }
			            
	            // egy sor üresen marad
                    htmlStringBuilder.append("<tr><td></td><td></td><td></td></tr>");
		
                    // hozzáadjuk a végösszeget
	            htmlStringBuilder.append("<tr><td></td><td>Végösszeg: </td><td>"+vegosszeg+"HUF</td></tr>");
			            
                    htmlStringBuilder.append("</table></body></html>");

                    //html fájlba írása
	            elkeszitettSzamla = szamlaKeszito(htmlStringBuilder.toString(),""+name.substring(0,3)+"_"+szamlaId+".html");
			            
                } catch (IOException e) {
                    e.printStackTrace();
	        }
            }

    } catch (SQLException e) {
	e.printStackTrace();
	return null;
    } finally {
	dbc.closeConnection(con);
	System.out.println(szamlaId);
    }
	return elkeszitettSzamla;
}
	
private String szamlaKeszito(String tartalom, String fileNev) throws IOException {
	//String utvonal = ".";
	String utvonal = "C:\\Users\\Vero\\eclipse-workspace\\SzamlazzHUProbafeladat\\WebContent\\szamla";
        String tempFile = utvonal + File.separator+fileNev;
        File file = new File(tempFile);

	if (file.exists()) {
	   try {
	       File newFileName = new File(utvonal + File.separator+ "backup_"+fileNev);
	       file.renameTo(newFileName);
	       file.createNewFile();
	  } catch (IOException e) {
	       e.printStackTrace();
	  }
	}
	        //write to file with OutputStreamWriter
	  OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
	  Writer writer=new OutputStreamWriter(outputStream);
	  writer.write(tartalom);
	  writer.close();
	  return tempFile;
}  

public String feltolt(String email, String jelszo, String termeknev[], String ar[]){
    String msg ="";
    
    if(!email.contains("@")){
      msg = "Érvénytelen email cím!";
    }
    
    for(int i=0; i<ar.length; i++){        
       if(!szam(ar[i])){
        msg = "Az ár csak számokat tartalmazhat!";
      }
    }
    
    String sql = "select id from `felhasznalok` where email=? and jelszo=?";
    PreparedStatement statement;
    ResultSet result;

    boolean sikeres = false;

    Connection con = dbc.getConnection();

    try {

            // első prepared statement az felhasználóID kinyeréséhez
            statement = con.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, jelszo);
            result = statement.executeQuery();
            String osszTermek="";
            int osszAr=0;
            int szamlaId=0;

            // ha van találat, megvan az id
            if (result.next()) {
                    int id = result.getInt("id");
                    try {
                            // ebben az esetben megnézzük, hány terméket töltöttünk fel, elemenként hozzáadjuk az adatbázishoz
                    for(int i=0; i<termeknev.length; i++){
                            // prepared statement a termékek feltöltéséhez
                            statement = con.prepareStatement("insert into `termektabla` (termeknev, ar, felhasznaloID) values (?, ?, ?)");
                            statement.setString(1, termeknev[i]);
                            statement.setString(2, ar[i] +"");
                            statement.setString(3, id+"");
                            statement.execute();
                            // összefűzzük a termékek neveit, és összeadjuk az áraikat
                                    osszTermek+=termeknev[i]+" ";
                                    osszAr+=Integer.parseInt(ar[i]);
                            }
                            // harmadik ps-sel az első ps-sel kinyert id-val feltöltjük a szamlatabla táblába
                            statement = con.prepareStatement(
                                            "insert into `szamlatabla` (eladott_termekek, osszeg, felhasznaloID) values (?,?,?)", 
                                            Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, osszTermek);
                            statement.setString(2, osszAr+"");
                            statement.setString(3, id+"");
                            statement.executeUpdate();

                            //siker esetén elkérjük az újonnan generált szamlaID-t
                            result = statement.getGeneratedKeys();

                            if(result.next()){
                                    szamlaId= result.getInt(1);
                            }
                            sikeres =true;

                            // abszolút siker esetén kiállítunk egy számlát (html-t készítünk)
                            if(sikeres){
                                    szamlakiallit(email, termeknev, ar, osszAr, szamlaId, id);
                                    System.out.println("A szamla sikeresen elkeszult!");
                            }

                    } catch (SQLException e) {
                                        e.getMessage();
                    } finally {
                            dbc.closeConnection(con);
                            System.out.println(szamlaId);
                    }
            } else
                    msg = "Hibás belépési adatok!";
    } catch (SQLException e) {
            msg = e.getMessage();
            
    } finally {
            dbc.closeConnection(con);
    }
    
    return msg;
}

private boolean szam(String szam){
  if (szam== null){
    return false;
  }
    for (int i=0; i<szam.length(); i++){
      if(Character.isDigit(szam.charAt(i))==false){
        return false;
      }
    }
    return true;
}

}

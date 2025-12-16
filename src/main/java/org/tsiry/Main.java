
package org.tsiry;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        DBConnection db = new DBConnection();
        Connection con = db.getConnection();
        System.out.println(con);
    }
}

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by nikhilshekhar on 3/26/16.
 */
public class Test {
    public static void main(String[] args){
//        try {
//            BufferedReader br = new BufferedReader(new FileReader("/Users/nikhilshekhar/SharedFolder-VM/egfr_df_from_r.csv"));
//            br.readLine();
//            String[] onlyLine = br.readLine().split(",");
//            for(String column : onlyLine){
////                System.out.println(column);
//                if (column.startsWith("\"")){
//                    System.out.println(column);
//                    String newStirng = column.substring(1,column.lastIndexOf("\""));
//                    System.out.println(newStirng);
//                }
//
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        String s = "999adfg";
        System.out.println(s.substring(2));

    }
}

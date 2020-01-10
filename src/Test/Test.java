package Test;

import DLibX.*;

import java.io.*;
import java.util.*;
import java.awt.*;

// This is a super hacked togather test class launcher. dont worry about it.

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        final ArrayList<String> classes = new ArrayList<>();
        final char slash = (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0)? '\\':'/';

        int i = 0;

        File last = new File("./assets/last");
        if (last.isFile() && last.canRead()) {
            Scanner scanner = new Scanner(last);
            if (scanner.hasNextInt()) i = scanner.nextInt();
        }

        File f = new File("./src/Test");
        File[] files = f.listFiles();

        for (File file: files) {
            if (!file.isDirectory()) {
                String[] path = file.getCanonicalPath().split("\\.(?=[^\\.]+$)");
                path[0] = path[0].substring(path[0].lastIndexOf(slash) + 1);
                if (path.length > 1 && path[1].equals("java") && !path[0].equals("Test")) {
                    Scanner scanner = new Scanner(file);
                    boolean result = false;
                    while (scanner.hasNextLine() && !result) {
                        String line = scanner.nextLine();
                        if (line.contains("public static void main")) result = true;
                    }
                    if (result) classes.add(path[0]);
                }
            }
        }
        Collections.sort(classes);

        int t = 0;

        final DConsole dc = new DConsole("Test Program Luancher",400,300,true);
        dc.setTextAlignment(DConsole.ALIGN_CENTER);
        dc.setBackground(new Color(45,45,45));
        dc.setPaint(new Color(220,220,220));
        dc.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        dc.setRenderingHints(DConsole.RENDER_HIGH_QUALITY);

        while (classes.size() > 0) {
            dc.clear();

            if (dc.getKeyPress(10) || t == 1000) {
                dc.setVisible(false);

                BufferedWriter b = new BufferedWriter(new FileWriter(last));
                b.write(""+i);
                b.close();

                long time = System.currentTimeMillis();

                Process p = Runtime.getRuntime().exec(new String[]{"java", "-verbose:gc", "-cp", System.getProperty("java.class.path"), "Test."+classes.get(i)}); //
                SequenceInputStream s = new SequenceInputStream(p.getInputStream(), p.getErrorStream());
                Scanner scanner = new Scanner(s);

                while (scanner.hasNextLine()) {
                    System.out.println(scanner.nextLine());
                }
                scanner.close();
                p.waitFor();

                System.out.println();

                time = (System.currentTimeMillis() - time)/100;

                System.out.println("Finished in " + time/10 + "." + time%10 + " seconds!");
                System.exit(p.exitValue());
            }

            if (dc.getKeyPress(38)) {
                t = -1;
                i--;
            }
            if (dc.getKeyPress(40)) {
                i++;
                t = -1;
            }
            i = (i+classes.size())%classes.size();

            String str = "";
            for (int j = 0; j < classes.size(); j++) {
                str += (j==i?"< ":"")+classes.get(j)+(j==i?" >":"")+"\n";
            }
            str = str.substring(0,str.length()-1);

            dc.setOrigin(DConsole.ORIGIN_TOP);
            dc.drawString(str,dc.getWidth()/2,10);
            dc.setOrigin(DConsole.ORIGIN_BOTTOM_RIGHT);
            if (t >= 0) dc.drawString("Time Remaining: "+(50-t/20),dc.getWidth()-10,dc.getHeight()-10);

            dc.redraw();
            DConsole.pause(5);
            if (t >= 0) t++;
        }

        dc.setOrigin(DConsole.ORIGIN_TOP);

        while (true) {
            dc.clear();
            dc.drawString("No test classes found.\nAdd them to the src/Test/ folder",dc.getWidth()/2,10);
            dc.redraw();
            DConsole.pause(5);
        }
    }
}

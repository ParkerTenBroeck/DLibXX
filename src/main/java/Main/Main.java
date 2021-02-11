package Main;


import DLibX.DCanvas;
import DLibX.util.BezierCurve;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main { // this is all probably buggy, sorry
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Main m = new Main();
            }
        });
    }

    JFrame frame;
    JPanel bar;
    JScrollPane scroll;
    JPanel container;
    JSplitPane view;

    ArrayList<JButton> buttons;
    Thingy[] content;

    Main() {
        frame = null;
        bar = null;
        scroll = null;
        container = null;
        content = null;
        // view = null;
        buttons = null;

        frame = new JFrame();
        bar = new JPanel();
        scroll = new JScrollPane(bar);
        container = new JPanel();
        view = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bar, container);

        buttons = new ArrayList<>();
        {
            JButton temp = new JButton("Bezier Curve Editor");
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    bezier();
                }
            });
            buttons.add(temp);
        }
        {
            JButton temp = new JButton("Key Press Info");
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    keys();
                }
            });
            buttons.add(temp);
        }
        {
            JButton temp = new JButton("Regex Tester");
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    regex();
                }
            });
            buttons.add(temp);
        }
        {
            JButton temp = new JButton("Version");
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    version();
                }
            });
            buttons.add(temp);
        }
        content = new Thingy[buttons.size()];

        bar.setLayout(new GridLayout(buttons.size(),1));
        bar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bar.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setBackground(Color.WHITE);

        view.setDividerLocation(200);

        frame.setContentPane(new JPanel());
        ((JPanel)frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        for (JButton i: buttons)
            bar.add(i);

        frame.add(view);
        frame.setMinimumSize(new Dimension(600, 400));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        container.requestFocus();
    }

    void button(Thingy t) {
        container.removeAll();
        container.add(t.get());
        container.revalidate();
        container.repaint();
        t.get().requestFocus();
    }

    void version() {
        if (content[0] == null) content[0] = new Version();

        button(content[0]);
    }

    void keys() {
        if (content[1] == null) content[1] = new Keys();

        button(content[1]);
    }

    void bezier() {
        if (content[2] == null) content[2] = new Bezier();

        button(content[2]);
    }

    void regex() {
        if (content[3] == null) content[3] = new Regex();

        button(content[3]);
    }

    private abstract class Thingy {
        JComponent r;
        public JComponent get() {
            return this.r;
        }
    }

    private class Version extends Thingy {
        JTextArea t;
        JButton b;
        int u = -1;
        int build;
        String version, built;
        Version() {
            this.r = new JPanel();
            r.setLayout(new BorderLayout());
            r.add(t = new JTextArea(), BorderLayout.CENTER);
            r.add(b = new JButton("Click to check for updates"), BorderLayout.SOUTH);

            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        URL url = new URL("https://bitbucket.org/Parker1105/dlibxx/downloads/VERSION");
                        ReadableByteChannel ch = Channels.newChannel(url.openStream());
                        Scanner sc = new Scanner(ch);
                        if (sc.nextInt() > Version.this.build) {
                            Version.this.u = 1;
                        } else {
                            Version.this.u = 0;
                        }
                        sc.close();
                        Version.this.draw();
                    } catch (Exception ex) {
                        u = 2;
                    }
                }
            });

            {
                InputStream in = Main.class.getResourceAsStream("/META-INF/MANIFEST.MF");
                try {
                    Manifest manifest = new Manifest(in);
                    Attributes a = manifest.getMainAttributes();
                    String v = a.getValue("build");
                    build = Integer.parseInt(v);
                    version = a.getValue("version");
                    built = (new Date(Long.parseLong(a.getValue("built")))).toString();

                } catch (IOException e) {
                    System.err.println("Cannot read version information aborting");
                    System.exit(0);
                }finally {
                    try {
                        in.close();
                    }catch(Exception e){

                    }
                }
            }

            t.setBackground(Color.WHITE);
            t.setEditable(false);
            draw();
        }
        void draw() {
            String updates = "";
            switch (u) {
            case 0:
                updates = "\n\nNo updates available.";
                break;
            case 1:
                updates = "\n\nUpdates available!\nGoto https://bitbucket.org/Parker1105/dlibxx/ to download.";
                break;
            case 2:
                updates = "\n\nCould not connect to server.";
            }
            t.setText("Library Version Data\n==========\nVersion " + version + "\nBuild #" + build + "\nBuilt " + built + updates);
        }
    }

    private class Keys extends Thingy {
        JTextArea t;
        Keys() {
            r = new JTextArea();
            t = (JTextArea)r;

            t.setBackground(Color.WHITE);
            t.setEditable(false);
            t.setText("Key Press Info\n==========\nPress a Key to Get Info");

            t.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    String data = e.paramString().replaceAll("(,([^']))", "\n$2").replaceAll("=", " = ");
                    Keys.this.t.setText("Key Press Info\n==========\n" + data);
                }
            });
        }
    }

    private class Bezier extends Thingy { // hope this all works
        JPanel p;
        JPanel pb;
        JTextField t;
        JButton g;
        DCanvas bc;
        DCanvas dc;
        ArrayList<Point2D> points;
        int grabbed = -1;
        int sx = 0;
        int sy = 0;
        Bezier() {
            points = new ArrayList<>();
            r = new JPanel();
            p = new JPanel() {
                public static final long serialVersionUID = 34742732665L;
                @Override
                public void paintComponent(Graphics g) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0,0,this.getWidth(), this.getHeight());
                    g.drawImage(Bezier.this.dc.getImage(), Bezier.this.sx, Bezier.this.sy, this);
                    Toolkit.getDefaultToolkit().sync();
                }
                @Override
                public void update(Graphics g) {
                    paintComponent(g);
                }
            };
            dc = new DCanvas(1, 1, DCanvas.ALPHA_OPAQUE);
            bc = new DCanvas(1, 1, DCanvas.ALPHA_TRANSLUCENT);
            p.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    Bezier.this.fix();
                }
                @Override
                public void componentResized(ComponentEvent e) {
                    Bezier.this.fix();
                }
            });
            p.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Bezier.this.draw(e.getX()-sx, e.getY()-sy, false);
                    Bezier.this.p.repaint();
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    Bezier.this.draw(e.getX()-sx, e.getY()-sy, true);
                    Bezier.this.p.repaint();
                }
            });
            p.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Bezier.this.p.requestFocus();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    Bezier.this.draw(e.getX()-sx, e.getY()-sy, true);
                    Bezier.this.p.repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Bezier.this.point(e.getX()-sx, e.getY()-sy);
                    }
                    Bezier.this.drawCurve();
                    Bezier.this.draw(e.getX(), e.getY()-sy, false);
                    Bezier.this.p.repaint();
                }
            });
            r.setLayout(new BorderLayout());
            r.add(p, BorderLayout.CENTER);
            r.add(pb = new JPanel(), BorderLayout.SOUTH);
            pb.setBackground(Color.WHITE);
            pb.setLayout(new BorderLayout());
            pb.add(t = new JTextField(), BorderLayout.CENTER);
            t.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Bezier.this.makeCurve();
                }
            });
            t.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    Bezier.this.t.select(0, Bezier.this.t.getText().length());
                }

                @Override
                public void focusLost(FocusEvent e) {
                    Bezier.this.t.select(0, 0);
                }
            });

            bc.setPlane(DCanvas.PLANE_CARTESIAN);
            dc.setOrigin(DCanvas.ORIGIN_CENTER);
            bc.setRenderingHints(DCanvas.RENDER_HIGH_QUALITY);
            bc.setCorrectionMode(true);

            dc.setPlane(DCanvas.PLANE_CARTESIAN);
            dc.setRenderingHints(DCanvas.RENDER_HIGH_QUALITY);
            dc.setCorrectionMode(true);

            points.add(new Point2D.Double(0,0));
            points.add(new Point2D.Double(0.42, 0));
            points.add(new Point2D.Double(0.58, 1));
            points.add(new Point2D.Double(1,1));
        }
        void fix() {
            Dimension d = p.getSize();
            int m = (int)Math.min(d.getWidth(), d.getHeight());
            sx = (int)((d.getWidth()-m)/2);
            sy = (int)((d.getHeight()-m)/2);
            dc.setSize(m, m);
            bc.setSize(m, m);
            drawCurve();
            draw(-1,-1,false);
            p.repaint();
        }
        void point(int mx, int my) {
            my = dc.getHeight()-my;
            double[] xs = new double[points.size()];
            double[] ys = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                Point2D t = points.get(i);
                xs[i] = t.getX()*(dc.getWidth()*8/10)+dc.getWidth()/10;
                ys[i] = t.getY()*(dc.getHeight()*8/10)+dc.getHeight()/10;
            }
            int b = -1;
            for (int i = 0; (b == -1) && (i < points.size()); i++)
                if (Point2D.distance(mx, my, xs[i], ys[i]) < 5)
                    b = i;
            if (b != -1) {
                if (points.size() > 2)
                    points.remove(b);
            } else {
                Point2D temp = new Point2D.Double((mx-dc.getWidth()/10.0)/(dc.getWidth()*8/10), (my-dc.getHeight()/10.0)/(dc.getHeight()*8/10));
                double[] l = new double[points.size()-1];
                for (int i = 1; i < points.size(); i++)
                    l[i-1] = Line2D.ptSegDist(points.get(i-1).getX(), points.get(i-1).getY(), points.get(i).getX(), points.get(i).getY(), temp.getX(), temp.getY());

                int x = 0;
                for (int i = 1; i < l.length; i++) {
                    if (l[i] < l[x])
                        x = i;
                }
                points.add(x+1, temp);
            }
            grabbed = -1;
        }
        void draw(int mx, int my, boolean mb) {
            dc.clear();
            // dc.setOrigin()
            my = dc.getHeight()-my;

            double[] xs = new double[points.size()];
            double[] ys = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                Point2D t = points.get(i);
                xs[i] = t.getX()*(dc.getWidth()*8/10)+dc.getWidth()/10;
                ys[i] = t.getY()*(dc.getHeight()*8/10)+dc.getHeight()/10;
            }

            int b = -1;
            for (int i = 0; (b == -1) && (i < points.size()); i++)
                if (Point2D.distance(mx, my, xs[i], ys[i]) < 5)
                    b = i;

            if (mb) {
                if (grabbed == -1) {
                    grabbed = b;
                } else {
                    b = grabbed;
                }
            } else {
                grabbed = -1;
            }

            if (grabbed != -1) {
                points.get(grabbed).setLocation((mx-dc.getWidth()/10.0)/(dc.getWidth()*8/10), (my-dc.getHeight()/10.0)/(dc.getHeight()*8/10));
                xs[grabbed] = mx;
                ys[grabbed] = my;
            }

            dc.setStroke(new BasicStroke(1));
            dc.setPaint(new Color(0f,0f,0f,0.5f));
            dc.drawPolyline(xs, ys);

            dc.drawImage(bc.getImage(), dc.getWidth()/2, dc.getHeight()/2);

            dc.setStroke(new BasicStroke(2));
            dc.setPaint(Color.RED);


            for (int i = 0; i < points.size(); i++)
                if (b == i) {
                    dc.fillEllipse(xs[i], ys[i], 12, 12);
                } else {
                    dc.drawEllipse(xs[i], ys[i], 10, 10);
                }

            if (b != -1) {
                p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                p.setCursor(Cursor.getDefaultCursor());
            }

        }
        void drawCurve() {
            bc.clear();

            int w = dc.getWidth()*8/10;
            int h = dc.getHeight()*8/10;
            int ox = dc.getWidth()/10;
            int oy = dc.getHeight()/10;

            bc.setStroke(new BasicStroke(1));
            bc.setPaint(new Color(0f,0f,0f,0.1f));
            for (int i = 0; i <= 10; i++) {
                bc.drawLine(ox+(i*w/10), oy, ox+(i*w/10), oy+h);
                bc.drawLine(ox, oy+(i*h/10), ox+w, oy+(i*h/10));
            }

            Point2D[] ps = new Point2D[w];
            for (int i = 0; i < w; i++) {
                Point2D[] pt = points.toArray(new Point2D[points.size()]);

                Point2D t = BezierCurve.getPoint((double)i/w, pt);
                t.setLocation(t.getX()*w+ox, t.getY()*h+oy);
                ps[i] = t;
            }

            bc.setStroke(new BasicStroke(3));
            bc.setPaint(Color.BLUE);
            bc.drawPolyline(ps);

            String s = "";
            s += "(" + makeString(points.get(0).getX()) + ", " + makeString(points.get(0).getY()) + ")";
            for (int i = 1; i < points.size(); i++)
                s += ", (" + makeString(points.get(i).getX()) + ", " + makeString(points.get(i).getY()) + ")";
            this.t.setText(s);
            t.revalidate();
        }
        String makeString(double n) {
            String s = String.format("%.3f", n);
            while (s.charAt(s.length()-1) == '0')
                s = s.substring(0, s.length()-1);
            if (s.charAt(s.length()-1) == '.')
                s = s.substring(0, s.length()-1);
            return s;
        }
        void makeCurve() {
            Point2D[] pts = null;

            try {
                String[] a = t.getText().replaceAll("[^-?(?)?.?,?0-9]", "").split("\\),\\(");
                if (a.length < 2) throw new Exception("");
                pts = new Point2D[a.length];

                for (int i = 0; i < a.length; i++) {
                    String[] temp = a[i].replaceAll("[(?)?]", "").split(",");
                    pts[i] = new Point2D.Double(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
                }
            } catch (Exception e) {
                pts = null;
            }

            if (pts != null) {
                points.clear();
                points.addAll(Arrays.asList(pts));
            }

            drawCurve();
            if (pts == null) t.setText(t.getText() + " // Improper curve format!");
            draw(-1,-1,false);
            p.repaint();
        }
    }


    private class Regex extends Thingy {
        JPanel panel;
        JPanel topPanel;
        JPanel buttonPanel;
        JPanel fieldPanel;

        JTextArea in;
        JTextArea out;
        JTextArea groups;

        JSplitPane mainSplit;
        JSplitPane subSplit;

        JButton regexButton;
        JButton replaceButton;

        JTextField regexField;
        JTextField replaceField;

        Pattern pattern;

        Regex() {
            Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);

            this.r = panel = new JPanel();
            topPanel = new JPanel();
            buttonPanel = new JPanel();
            fieldPanel = new JPanel();

            topPanel.setLayout(new BorderLayout());
            buttonPanel.setLayout(new BorderLayout());
            fieldPanel.setLayout(new BorderLayout());
            panel.setLayout(new BorderLayout());

            buttonPanel.add(regexButton = new JButton("Find"), BorderLayout.NORTH);
            buttonPanel.add(replaceButton = new JButton("Replace"), BorderLayout.SOUTH);

            fieldPanel.add(regexField = new JTextField(), BorderLayout.NORTH);
            fieldPanel.add(replaceField = new JTextField(), BorderLayout.SOUTH);


            topPanel.add(buttonPanel, BorderLayout.EAST);
            topPanel.add(fieldPanel, BorderLayout.CENTER);

            panel.add(topPanel, BorderLayout.NORTH);

            subSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(in = new JTextArea()), new JScrollPane(out = new JTextArea()));
            mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subSplit, new JScrollPane(groups = new JTextArea()));

            subSplit.setDividerLocation(Main.this.container.getHeight()/2);
            mainSplit.setDividerLocation(Main.this.container.getWidth()*2/3);

            in.setText("is regex cool?\nis regex fun?\nis regex useful?");
            out.setText("Press replace to see the answer!");
            regexField.setText("(is )(regex )(\\w*).");
            replaceField.setText("$2$1really $3!");


            in.setToolTipText("Input string to be matched");
            out.setToolTipText("Output of a replacement");
            groups.setToolTipText("List of capture groups and their values");
            regexField.setToolTipText("Regular Expression to be evaluated");
            replaceField.setToolTipText("Replacement string");

            regexButton.setFont(mono);
            replaceButton.setFont(mono);
            in.setFont(mono);
            out.setFont(mono);
            groups.setFont(mono);
            regexField.setFont(mono);
            replaceField.setFont(mono);

            panel.add(mainSplit, BorderLayout.CENTER);

            regexField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        regex();
                    }
                }
            });
            replaceField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10) {
                        replace();
                    }
                }
            });

            regexButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Regex.this.regex();
                }
            });
            replaceButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Regex.this.replace();
                }
            });
        }

        boolean regex() {
            pattern = null;
            try {
                pattern = Pattern.compile(regexField.getText());
            } catch (Exception e) {
                out.setText("Invalid Regex Pattern!\n" + e.getMessage());
                groups.setText("");
                return false;
            }
            Matcher match = pattern.matcher(in.getText());
            String o = "";
            if (match.find()) {
                o += "Matches found!\nGroups as follows:\n";
                do {
                    o += "-----\n";
                    for (int i = 0; i <= match.groupCount(); i++) {
                        String t = match.group(i);
                        if (t != null) o += i + "> `" + t + "` [" + match.start(i) + " - " + match.end(i) + "]\n";
                    }
                } while (match.find());
                out.setText("Matches found!");
            } else {
                out.setText("No matches found!");
                groups.setText("No matches found!");
                return false;
            }
            groups.setText(o);
            return true;
        }

        void replace() {
            if (regex()) {
                try {
                    Matcher match = pattern.matcher(in.getText());
                    out.setText(match.replaceAll(Unescape.unescape_perl_string(replaceField.getText())));
                } catch (Exception e) {
                    out.setText("Invalid Replacement Pattern!\n" + e.getMessage());
                    groups.setText("");
                }
            }
        }
    }
}

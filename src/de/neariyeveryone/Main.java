package de.neariyeveryone;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

class Main {
    private static final List<Agent> list = new ArrayList<>();
    private static final String[] agents = {
            "astra",
            "breach",
            "brimstone",
            "chamber",
            "cypher",
            "fade",
            "gekko",
            "harbor",
            "jett",
            "kayo",
            "killjoy",
            "neon",
            "omen",
            "phoenix",
            "raze",
            "reyna",
            "sage",
            "skye",
            "sova",
            "viper",
            "yoru",

            "LOCK IN",
    };
    private static Gson gson2;
    private static SQLite db;

    public static void main(String[] args) {
        setup();

        db.connect();
    }

    private static void setup() {
        gson2 = new Gson();
        db = new SQLite(String.format("%s%s\\deckboard\\database.db", "jdbc:sqlite:",System.getProperty("user.home")));

        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setOpacity(0.2f);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    handleClick(e);
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static void handleClick(MouseEvent e) throws SQLException, IOException {
        if (e.getButton() == MouseEvent.BUTTON1) {
            list.add(new Agent(agents[list.size()], e.getX(), e.getY()));
            System.out.printf("%s | Position for %s set at %s, %s%n", list.size(), agents[list.size() - 1], e.getX(), e.getY());
        }else if (e.getButton() == MouseEvent.BUTTON2) {
        	db.disconnect();
                System.exit(0);
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            ResultSet set = db.query("SELECT COUNT(*) from Boards");
            int order = 999;
            if (set.next()) {
                order = set.getInt("COUNT(*)");
            }
            db.update(String.format("INSERT INTO Boards(name, background, `order`, layout, image, width, height, converted) VALUES('%s', '%s', %s, %s, '%s', %s, %s, %s)", "instalock", "#000000", order, 6, "", 11, 3, 1));
            set = db.query("SELECT MAX(id) from Boards");
            int id = 999;
            if (set.next()) {
                id = set.getInt("MAX(id)");
            }
            List<List<MultiAction>> actions = new ArrayList<>();
            List<String> icons = new ArrayList<>();
            for (Agent agent : list) {
                List<MultiAction> action = new ArrayList<>();
                if (!agent.name().equals("LOCK IN")) {
                    var command = gson2.toJson(new Command(Action.move, String.valueOf(agent.x()), String.valueOf(agent.y())));
                    action.add(new MultiAction("mouse-ctrl", command, null));
                    command = gson2.toJson(new Command(Action.lclick, null, null));
                    action.add(new MultiAction("mouse-ctrl", command, null));
                    command = gson2.toJson(new Command(Action.move, String.valueOf(list.get(agents.length - 1).x()), String.valueOf(list.get(agents.length - 1).y())));
                    action.add(new MultiAction("mouse-ctrl", command, null));
                    command = gson2.toJson(new Command(Action.lclick, null, null));
                    action.add(new MultiAction("mouse-ctrl", command, null));
                    actions.add(action);
                    icons.add(String.format("data:image/jpeg;base64,%s", Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File("img/" + agent.name() + ".webp")))));
                }
            }
            for (List<MultiAction> action : actions) {
            	System.out.println(String.format("INSERT INTO Shortcuts(board_id, title, img, type, command, position, title_position, title_color, title_box_color, options, color, border_color, shape, icon, img2, icon2, color2, shape2, border_color2, title_position2, title_box_color2, title_color2, position2, icon_color, icon_color2) VALUES(%s, %s, '%s', '%s', '%s', %s, %s, '%s', '%s', %s, '%s', '%s', %s, %s, '%s', %s, %s, %s, '%s', %s, '%s', '%s', %s, '%s', '%s')", id, null, icons.get(actions.indexOf(action)), "multiaction", gson2.toJson(action), actions.indexOf(action), 0, "#ffffff", "", null, "#2e313100", "", 0, null, "", null, null, 0, "", 0, "", "#ffffff", null, "", ""));
                db.update(String.format("INSERT INTO Shortcuts(board_id, title, img, type, command, position, title_position, title_color, title_box_color, options, color, border_color, shape, icon, img2, icon2, color2, shape2, border_color2, title_position2, title_box_color2, title_color2, position2, icon_color, icon_color2) VALUES(%s, %s, '%s', '%s', '%s', %s, %s, '%s', '%s', %s, '%s', '%s', %s, %s, '%s', %s, %s, %s, '%s', %s, '%s', '%s', %s, '%s', '%s')", id, null, icons.get(actions.indexOf(action)), "multiaction", gson2.toJson(action), actions.indexOf(action), 0, "#ffffff", "", null, "#2e313100", "", 0, null, "", null, null, 0, "", 0, "", "#ffffff", null, "", ""));
            }

            db.disconnect();
            System.exit(0);
        }
    }
}
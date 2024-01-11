package Emoji;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.vdurmont.emoji.EmojiParser;
public class EmojiPicker extends JPanel {
    //private static JList<String> emojiList;
    private JList<String> emojiList;

    public EmojiPicker() {
        List<String> emojis = new ArrayList<>();
        String[] emojiHtmlList = new String[]{"&#128514;","&#10084;","&#128525;","&#129315;","&#128522;",
                "&#128591;","&#128149;","&#128557;","&#128293;","&#128536;","&#128077;","&#129392;","&#128526;","&#128518;",
                "&#128513;","&#128521;","&#129300;","&#128517;","&#128532;","&#128580;","&#128540;","&#9829;","&#9851;","&#128530;",
                "&#128553;","&#9786;","&#128513;","&#128076;","&#128079;","&#128148;","&#128150;","&#128153;",
                "&#128546;","&#128170;","&#129303;","&#128156;","&#128526;","&#128519;","&#127801;","&#129318;",
                "&#127881;","&#128158;","&#9996;","&#10024;","&#129335;","&#128561;","&#128524;","&#127800;",
                "&#128588;","&#128523;","&#127770;","&#127773;","&#128584;","&#128585;","&#128586;"};
        for (String em:emojiHtmlList) {
            emojis.add(EmojiParser.parseToUnicode(em));
        }
        System.out.println("Parsed emojis: " + emojis);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String emoji : emojis) {
            model.addElement(emoji);
        }

        emojiList = new JList<>();
        emojiList.setModel(model);
        emojiList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        emojiList.setVisibleRowCount(-1);

        // Set the layout for the picker
        setLayout(new BorderLayout());
        add(new JScrollPane(emojiList), BorderLayout.CENTER);
    }

    public JList<String> getEmojiList() {
        return emojiList;
    }

}

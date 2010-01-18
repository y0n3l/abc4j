package abcynth;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import abc.util.PropertyManager;

/**
 * Ability for user to choose (in no particular order):
 *   Font size
 *   Titles displayed (yes,no)
 *   Stem direction (auto, up, down)
 *   Engraving style (justified, by note duration)

 *   restore window on start
 *   open last tunebook on start
 *   save settings on exit

 *   MIDI Voice
 *   Gracenote display style
 *   Gracenote duration
 */
public class PrefsDialog extends JDialog implements ActionListener {

  // FIXME: needs unique UID
  private static final long serialVersionUID = -7024807010515214434L;

  private static final String GENERAL_TAB_NAME = "General";
  private static final String PROFILE_TAB_NAME = "Profiles";


  private JTabbedPane m_tabbedPane = null;
  private JPanel m_general, m_profile = null;
  private JComboBox m_scoreSize, m_lang = null;
  private JRadioButton m_titleYes, m_titleNo = null;
  private JRadioButton m_stemAuto, m_stemUp, m_stemDown = null;
  private JRadioButton m_engraveAuto, m_engraveJust, m_engraveFix = null;

  private JButton saveButton = null;
  private JButton cancelButton = null;
  private boolean answer = false;

  public PrefsDialog(JFrame frame, String title, boolean modal) {
    super(frame, title, modal);
    m_general = new JPanel();
	m_general.setLayout(new SpringLayout());

    m_profile = new JPanel();

    m_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
//    m_tabbedPane.setPreferredSize(new Dimension(200,200));

    m_tabbedPane.addTab(GENERAL_TAB_NAME, new JScrollPane(m_general, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    m_tabbedPane.addTab(PROFILE_TAB_NAME, new JScrollPane(m_profile, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    getContentPane().add(m_tabbedPane);


    JLabel sizeLabel = new JLabel("Size:", JLabel.TRAILING);
    m_general.add(sizeLabel);
   	String [] sizes = {"50 %", "75 %", "100 %","125 %", "150 %", "200 %" };
	m_scoreSize = new JComboBox(sizes);
	m_scoreSize.setSelectedIndex(2);
    sizeLabel.setLabelFor(m_scoreSize);
	m_general.add(m_scoreSize);

    JLabel langLabel = new JLabel("Language:", JLabel.TRAILING);
    m_general.add(langLabel);
   	String [] langs = {"English"};
	m_lang = new JComboBox(langs);
	m_lang.setSelectedIndex(0);
    langLabel.setLabelFor(m_lang);
	m_general.add(m_lang);

    JLabel titleLabel = new JLabel("Display titles:", JLabel.TRAILING);
    m_general.add(titleLabel);
    ButtonGroup titleButtonsGroup = new ButtonGroup();
	JPanel titleButtonsPanel = new JPanel();
	m_titleYes = new JRadioButton("Yes");
	m_titleNo = new JRadioButton("No");
    titleButtonsGroup.add(m_titleYes);
    titleButtonsGroup.add(m_titleNo);
    titleButtonsPanel.add(m_titleYes);
    titleButtonsPanel.add(m_titleNo);
    titleLabel.setLabelFor(titleButtonsPanel);
    m_general.add(titleButtonsPanel);


    JLabel stemLabel = new JLabel("Stem direction:", JLabel.TRAILING);
    m_general.add(stemLabel);
    ButtonGroup stemButtonsGroup = new ButtonGroup();
	JPanel stemButtonsPanel = new JPanel();
	m_stemAuto = new JRadioButton("Automatic");
	m_stemUp = new JRadioButton("Up");
	m_stemDown = new JRadioButton("Down");
    stemButtonsGroup.add(m_stemAuto);
    stemButtonsGroup.add(m_stemUp);
    stemButtonsGroup.add(m_stemDown);
    stemButtonsPanel.add(m_stemAuto);
    stemButtonsPanel.add(m_stemUp);
    stemButtonsPanel.add(m_stemDown);
    stemLabel.setLabelFor(stemButtonsPanel);
    m_general.add(stemButtonsPanel);


    JLabel engraveLabel = new JLabel("Note spacing:", JLabel.TRAILING);
    m_general.add(engraveLabel);
    ButtonGroup engraveButtonsGroup = new ButtonGroup();
	JPanel engraveButtonsPanel = new JPanel();
	m_engraveAuto = new JRadioButton("Automatic");
	m_engraveJust = new JRadioButton("Justified");
	m_engraveFix = new JRadioButton("Fixed");
    engraveButtonsGroup.add(m_engraveAuto);
    engraveButtonsGroup.add(m_engraveJust);
    engraveButtonsGroup.add(m_engraveFix);
    engraveButtonsPanel.add(m_engraveAuto);
    engraveButtonsPanel.add(m_engraveJust);
    engraveButtonsPanel.add(m_engraveFix);
    engraveLabel.setLabelFor(engraveButtonsPanel);
    m_general.add(engraveButtonsPanel);


    m_general.add(new JLabel(""));
    JPanel saveCancelPanel = new JPanel();
    saveButton = new JButton("Save");
    saveButton.addActionListener(this);
    saveCancelPanel.add(saveButton);
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    saveCancelPanel.add(cancelButton);
    m_general.add(saveCancelPanel);

	//Lay out the panel.
	SpringUtilities.makeCompactGrid(m_general,
                                6, 2, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad

    pack();
    setLocationRelativeTo(frame);

  }

  public void setVisible(boolean b) {
	if (b) {
      // load properties into gui fields
	  try {
		PropertyManager pm = PropertyManager.getInstance();
 		String val = null;

		val = (String)pm.getProperty(StringConstants.PROPS_KEY_LANG);
		if (val != null) {
			val = val.toLowerCase();
		}

		val = pm.getProperty(StringConstants.PROPS_KEY_SCORESIZE);
		if (val != null) {
		  // add " %" to property value
		  if (!val.endsWith(" %"))
		    val = val + " %";
		  // set combo box value
		  String itemVal = null;
		  int len = m_scoreSize.getItemCount();
		  for (int i=0; i< len; i++) {
		    itemVal = (String)m_scoreSize.getItemAt(i);
		    if (itemVal.equals(val)) {
		      m_scoreSize.setSelectedIndex(i);
		      break;
		    }
		  }
		}

		val = pm.getProperty(StringConstants.PROPS_KEY_DISPLAYTITLES);
		if (val != null) {
		  val = val.toLowerCase();
		  if (val.equals("yes"))
		    m_titleYes.setSelected(true);
		  else
		    m_titleNo.setSelected(true);
		}

		val = pm.getProperty(StringConstants.PROPS_KEY_STEMMINGPOLICY);
		if (val != null) {
          val = val.toLowerCase();
		  if (val.equals("up"))
		    m_stemUp.setSelected(true);
		  else if (val.equals("down"))
		    m_stemDown.setSelected(true);
		  else
		    m_stemAuto.setSelected(true);
		}

		val = pm.getProperty(StringConstants.PROPS_KEY_ENGRAVINGSTYLE);
		if (val != null) {
			val = val.toLowerCase();
		  if (val.equals("justified"))
		    m_engraveJust.setSelected(true);
		  else if (val.equals("fixed"))
		    m_engraveFix.setSelected(true);
		  else
		    m_engraveAuto.setSelected(true);
		}

	  } catch (Throwable th) {
	  	/* TJM */
	  	th.printStackTrace();
	  }

	} else {
      // if unsaved changes prompt save/discard
	}
	super.setVisible(b);
  }


  public void actionPerformed(ActionEvent e) {
    if(saveButton == e.getSource()) {
      try {
		// SCORE SIZE
		PropertyManager pm = PropertyManager.getInstance();
 		String val = null;
	    val = (String)m_scoreSize.getSelectedItem();
		pm.setProperty(StringConstants.PROPS_KEY_SCORESIZE, val);

		// TITLE DISPLAY
		if (m_titleYes.isSelected())
		  pm.setProperty(StringConstants.PROPS_KEY_DISPLAYTITLES, "yes");
		else
		  pm.setProperty(StringConstants.PROPS_KEY_DISPLAYTITLES, "no");

		// STEMMING POLICY
		if (m_stemUp.isSelected())
		  pm.setProperty(StringConstants.PROPS_KEY_STEMMINGPOLICY, "up");
		else if (m_stemDown.isSelected())
		  pm.setProperty(StringConstants.PROPS_KEY_STEMMINGPOLICY, "down");
		else
		  pm.setProperty(StringConstants.PROPS_KEY_STEMMINGPOLICY, "auto");

		// ENGRAVING STYLE
		if (m_engraveJust.isSelected())
		  pm.setProperty(StringConstants.PROPS_KEY_ENGRAVINGSTYLE, "justified");
		else if (m_engraveFix.isSelected())
		  pm.setProperty(StringConstants.PROPS_KEY_ENGRAVINGSTYLE, "fixed");
		else
		  pm.setProperty(StringConstants.PROPS_KEY_ENGRAVINGSTYLE, "auto");

	  } catch (IOException ex) {
		  // TODO: log or UI message
	  }
      setVisible(false);
    }
    else if(cancelButton == e.getSource()) {
      System.err.println("User chose no.");
      answer = false;
      setVisible(false);
    }
  }

}

package abc.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import abc.notation.ScoreElementInterface;
import abc.notation.Tune;
import abc.ui.swing.score.JTune;
import abc.ui.swing.score.ScoreMetrics;

/**
 * This JComponent displays tunes scores.
 * <BR/> You can get them rendered like :
 * <IMG src="../../../images/scoreEx.jpg"/> 
 * <BR/>
 * To render a tune score, just invoke the <TT>setTune(Tune)</TT> method
 * with your tune.<BR/>
 * Basically, a score if composed of {@link abc.ui.swing.JScoreElementAbstract "score elements"}
 * @see Tune 
 * 
 */
public class JScoreComponent extends JComponent {
	private static final Color SELECTED_ITEM_COLOR = Color.RED;
	/** The graphical representation of the tune currently set.
	 * <TT>null</TT> if no tune is set. */
	protected JTune m_jTune = null;
	
	//private int staffLinesOffset = -1;
	/** The dimensions of this score. */
	protected Dimension m_dimension = null;
	/** WTF ??? does not seem to be really taken into account anyway... */
	private int XOffset = 0;
	/** The place where all spacing dimensions are expressed. */
	protected ScoreMetrics m_metrics = null;
	/** The buffer where the score image is put before rendition in the swing component. */
	protected BufferedImage m_bufferedImage = null;
	/** The graphic context of the buffered image used to generate the score. */
	protected Graphics2D m_bufferedImageGfx = null;
	/** Set to <TT>true</TT> if the score drawn into the buffered image is
	 * outdated and does not represent the tune currently set. */
	protected boolean m_isBufferedImageOutdated = true;
	/** The size used for the score scale. */
	protected float m_size = 45;
	/** <TT>true</TT> if the rendition of the score should be justified, 
	 * <TT>false</TT> otherwise. */
	protected boolean m_isJustified = false;
	/** The selected item in this score. <TT>null</TT> if no 
	 * item is selected. */
	protected JScoreElementAbstract m_selectedItem = null;
	
	/** Default constructor. */
	public JScoreComponent() {
		m_dimension = new Dimension(1,1);
		initGfx();
	}
	
	protected void initGfx(){
		m_bufferedImage = new BufferedImage((int)m_dimension.getWidth(), (int)m_dimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
		m_bufferedImageGfx = (Graphics2D)m_bufferedImage.createGraphics();
		m_metrics = new ScoreMetrics(m_bufferedImageGfx, m_size);
	}
	
	/** Draws the current tune score into the given graphic context.
	 * @param g Graphic context. */
	protected void drawIn(Graphics2D g){
		if(m_jTune!=null) {
			m_jTune.render(g);
		}
	}
	
	public void paint(Graphics g){
		if (m_isBufferedImageOutdated) {
			//System.out.println("buf image is outdated");
			if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth() 
					|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
				initGfx();
			}
			m_bufferedImageGfx.setColor(getBackground());
			m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
			drawIn(m_bufferedImageGfx);
			m_isBufferedImageOutdated=false;
		}
		((Graphics2D)g).drawImage(m_bufferedImage, 0, 0, null);
		//if (m_jTune!=null)
		//	m_jTune.render((Graphics2D)g);
	}
	
	/** The size of the font used to display the music score.
	 * @param size The size of the font used to display the music score expressed in ? */
	public void setSize(float size){
		m_size = size;
		initGfx();
		if (m_jTune!=null)
			setTune(m_jTune.getTune());
		repaint();
	}
	
	/** Writes the currently set tune score to a PNG file.
	 * @param file The PNG output file.
	 * @throws IOException Thrown if the given file cannot be accessed. */ 
	public void writeScoreTo(File file) throws IOException {
		//if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth() 
		//		|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
			initGfx();
		//}
		m_bufferedImageGfx.setColor(getBackground());
		//m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
		m_bufferedImageGfx.setComposite(AlphaComposite.Clear);
		m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
		m_bufferedImageGfx.setComposite(AlphaComposite.SrcOver);
		drawIn(m_bufferedImageGfx);
		m_isBufferedImageOutdated=false;
		ImageIO.write(m_bufferedImage,"png",file);
	}
	
	/** Sets the tune to be renderered.
	 * @param tune The tune to be displayed. */
	public void setTune(Tune tune){
		if (m_metrics==null)
			m_metrics = new ScoreMetrics((Graphics2D)getGraphics());
		m_jTune = new JTune(tune, new Point(XOffset, 0), m_metrics, m_isJustified);
		m_selectedItem = null;
		m_dimension.setSize(m_jTune.getWidth(), m_jTune.getHeight()); 
				//componentHeight+m_metrics.getStaffCharBounds().getHeight());
		setPreferredSize(m_dimension);
		setSize(m_dimension);
		//if (m_isJustified)
		//	justify();
		m_isBufferedImageOutdated=true;
		repaint();
	}
	
	/** Changes the justification of the rendition score. This will
	 * set the staff lines aligment to justify in order to have a more
	 * elegant display. 
	 * @param isJustified <TT>true</TT> if the score rendition should be
	 * justified, <TT>false</TT> otherwise. 
	 * @see #isJustified()*/
	public void setJustification(boolean isJustified) {
		m_isJustified = isJustified;
		if (m_jTune!=null)
			setTune(m_jTune.getTune());
		//m_jTune = new m_jTune(m_jTune.getTune(), )
		//repaint();
	}
	
	/** Return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise.
	 * @return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise. 
	 * @see #setJustification(boolean) */
	public boolean isJustified() {
		return m_isJustified;
	}
	
	/** Returns the graphical score element fount at the given location.
	 * @param location A point in the score.
	 * @return The graphical score element found at the specified location.
	 * <TT>null</TT> is returned if no item is found at the given location. */
	public JScoreElementAbstract getScoreElementAt(Point location) {
		if (m_jTune!=null)
			return m_jTune.getScoreElementAt(location);
		else
			return null;
	}
	
	/** Highlights the given score element in the score.
	 * If an item was previously selected, this previous item
	 * is unselected.
	 * @param elmnt The music element to be highlighted in the
	 * score. <TT>null</TT> can be specified to remove 
	 * highlighting. 
	 * @see #setSelectedItem(JScoreElementAbstract) */
	public void setSelectedItem(ScoreElementInterface elmnt) {
		JScoreElementAbstract r = null;
		if (elmnt!=null)
			r = (JScoreElementAbstract)m_jTune.getRenditionObjectsMapping().get(elmnt);
		//if (r!=null)
		//	System.out.println("Selecting item " + elmnt + "->" + r + "@" + r.getBase());
		setSelectedItem(r);
	}
	
	/** Highlights the given score element in the score.
	 * If an item was previously selected, this previous item
	 * is unselected.
	 * @param elmnt The score rendition element to be highlighted in the
	 * score. <TT>null</TT> can be specified to remove 
	 * highlighting. 
	 * @see #setSelectedItem(ScoreElementInterface) */
	public void setSelectedItem(JScoreElementAbstract elmnt){
		if (m_selectedItem!=null) {
			m_bufferedImageGfx.setColor(Color.BLACK);
			m_selectedItem.render(m_bufferedImageGfx);
		}
		if (elmnt!=null) {
			m_selectedItem = elmnt;
			m_bufferedImageGfx.setColor(SELECTED_ITEM_COLOR);
			m_selectedItem.render(m_bufferedImageGfx);
		}
		repaint();
	}
	
	/** Returns the graphical element that corresponds to a tune element. 
	 * @param elmnt A tune element.
	 * @return The graphical score element that corresponds to the given 
	 * tune element. <TT>null</TT> is returned if the given tune element
	 * does not have any graphical representation. */
	public JScoreElementAbstract getRenditionElementFor(ScoreElementInterface elmnt) {
		if (m_jTune!=null) 
			return (JScoreElementAbstract)m_jTune.getRenditionObjectsMapping().get(elmnt);
		else
			return null;
	}

}

package ee.ioc.cs.vsle.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ViewportLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

/**
 * Swing UI for scrollable toolbar component.
 * In contrast of the original Simonis' implementation, a Swing Timer
 * is used for scrolling animation instead of a thread. This makes the
 * UI component thread safe.
 */
public class ScrollableBarUI extends ComponentUI 
		implements MouseListener, ChangeListener, ActionListener {

	private static final int BTN_SIZE = 14; // scrollbutton icon image size
	private static final int STEP = 20; // scrolling step

	// Scrolling animation constants
	private static final int INIT_DELAY = 200;
	private static final int MIN_DELAY = 30;
	private static final int ACCEL = 2;
	
	JViewport viewport;
	ScrollButton backBtn, forwardBtn;
	private ScrollableBar sb;
	private boolean btnVisible;
	private Timer timer;
	private JButton pressedBtn;

	public static ComponentUI createUI(JComponent c) {
		return new ScrollableBarUI();
	}

	@Override
	public void installUI(JComponent c) {
		sb = (ScrollableBar) c;

		forwardBtn = new ScrollButton(SwingConstants.RIGHT);
		backBtn = new ScrollButton(SwingConstants.LEFT);

		sb.setLayout(new BoxLayout(sb, BoxLayout.LINE_AXIS));

		viewport = new JViewport() {

			private static final long serialVersionUID = 1L;

			@Override
			protected LayoutManager createLayoutManager() {

				return new ViewportLayout() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Dimension minimumLayoutSize(Container parent) {
						Component view = ((JViewport) parent).getView();
						Dimension d;
						if (view == null) {
							d = new Dimension(4, 4);
						} else {
							d = view.getPreferredSize();
							d.width = 4;
						}
						return d;
					}
				};
			}
		};

		viewport.setView(sb.getComponent());
		viewport.setOpaque(false);

		sb.add(backBtn);
		sb.add(viewport);
		sb.add(forwardBtn);

		viewport.addChangeListener(this);
	}

	@Override
	public void uninstallUI(JComponent c) {
		viewport.removeChangeListener(this);
		if (timer != null && timer.isRunning())
			timer.stop();
	}

	public void mouseClicked(MouseEvent e) {
		// ignore
	}

	public void mouseEntered(MouseEvent e) {
		// ignore
	}

	public void mouseExited(MouseEvent e) {
		if (timer != null)
			timer.stop();
	}

	public void mousePressed(MouseEvent e) {
		if (timer == null) {
			timer = new Timer(INIT_DELAY, this);
			timer.setInitialDelay(0);
		} else
			timer.setDelay(INIT_DELAY);

		pressedBtn = (JButton) e.getSource();
		timer.start();
	}

	public void mouseReleased(MouseEvent e) {
		if (timer != null)
			timer.stop();
	}

	public void stateChanged(ChangeEvent e) {
		if (sb.getWidth() <= viewport.getView().getMinimumSize().width) {
			if (!btnVisible) { 
				btnVisible = true;
				forwardBtn.setVisible(true);
				backBtn.setVisible(true);

				// backbutton should be disabled when scrolling buttons appear
				backBtn.btn.setEnabled(false);
				forwardBtn.btn.setEnabled(true);
			}
		} else {
			if (btnVisible) {
				btnVisible = false;
				forwardBtn.setVisible(false);
				backBtn.setVisible(false);
				sb.doLayout();
			}
		}
	}

	class ScrollButton extends JPanel {

		private static final long serialVersionUID = 1L;

		/*
		 * LnF-s (most notably Motif) handle the sizes of borders differently
		 * for JButtons and JToggleButtons. It seems impossible to force a
		 * motif button to be less than 30px in height without reimplementing
		 * it while togglebuttons are 25px by default.
		 */
		JButton btn;

		private int[][] points = {
				// X coordinates
				{3, BTN_SIZE - 3, 3},
				// Y coordinates
				{3, BTN_SIZE / 2, BTN_SIZE - 3}
			};

		public ScrollButton(int direction) {
			switch (direction) {
				case SwingConstants.LEFT:
				case SwingConstants.RIGHT:
					break;
				default:
					throw new IllegalArgumentException();
			}
			init(direction);
		}

		private void init(int direction) {
			setVisible(false);
			setOpaque(false);

			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setBorder(new EmptyBorder(0, 0, 0, 0));

			// create icon for the button
			BufferedImage img = new BufferedImage(BTN_SIZE, BTN_SIZE,
					BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.setColor(Color.BLACK);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			if (direction == SwingConstants.LEFT) {
				g.translate(BTN_SIZE / 2, BTN_SIZE / 2);
				g.rotate(Math.PI);
				g.translate(-BTN_SIZE / 2, -BTN_SIZE / 2);
			}
			
			g.fillPolygon(points[0], points[1], points[0].length);
			
			btn = new JButton(new ImageIcon(img));
			btn.setMargin(new Insets(0, 0, 0, 0));
			btn.setFocusable(false);

			if (direction == SwingConstants.RIGHT)
				add(Box.createRigidArea(Palette.BUTTON_SPACE));
			
			add(btn);

			if (direction == SwingConstants.LEFT)
				add(Box.createRigidArea(Palette.BUTTON_SPACE));

			btn.addMouseListener(ScrollableBarUI.this);
		}
	}

	/**
	 * Implements scrolling of the toolbar. This method is called
	 * by Swing Timer. Therefore, it should execute quickly and it is safe to
	 * manipulate Swing GUI components from here.
	 */
	public void actionPerformed(ActionEvent e) {
		Point p = viewport.getViewPosition();
		if (pressedBtn == forwardBtn.btn) {
			backBtn.btn.setEnabled(true);
			if (viewport.getViewSize().width
					- viewport.getExtentSize().width
					- p.x > STEP) {
				p.x += STEP;
				viewport.setViewPosition(p);
				accelerate();
			} else {
				p.x = viewport.getViewSize().width 
					- viewport.getExtentSize().width;
				viewport.setViewPosition(p);
				forwardBtn.btn.setEnabled(false);
				timer.stop();
			}
		} else {
			forwardBtn.btn.setEnabled(true);
			if (p.x > STEP) {
				p.x -= STEP;
				viewport.setViewPosition(p);
				accelerate();
			} else {
				p.x = 0;
				viewport.setViewPosition(p);
				backBtn.btn.setEnabled(false);
				timer.stop();
			}
		}
	}

	/**
	 * Adjust timer's delay to create acceleration effect while scrolling.
	 */
	private void accelerate() {
		int delay = timer.getDelay();
		if (delay > MIN_DELAY) {
			delay /= ACCEL;
			if (delay < MIN_DELAY)
				delay = MIN_DELAY;
			timer.setDelay(delay);
		}
	}
}

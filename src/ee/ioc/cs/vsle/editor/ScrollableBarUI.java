package ee.ioc.cs.vsle.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import javax.swing.plaf.ComponentUI;

/**
 * Swing UI for scrollable toolbar component.
 * In contrast of the original Simonis' implementation, a Swing Timer
 * is used for scrolling animation instead of a custom thread. This makes the
 * UI component thread safe. The other problem with the original implementation
 * was that buttons set visible from stateChanged() method called by
 * JViewPort actually appeared only when the layout was changed once again.
 * The solution was to use ComponentListener instead of ChangeListener.
 */
public class ScrollableBarUI extends ComponentUI
		implements MouseListener, ComponentListener, ActionListener {

	private static final int BTN_SIZE = 14; // scrollbutton icon image size
	private static final int STEP = 20; // scrolling step

	// Scrolling animation constants
	private static final int INIT_DELAY = 200;
	private static final int MIN_DELAY = 30;
	private static final int ACCEL = 2;

	JViewport viewport;
	ScrollButton backPanel, forwardPanel;
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

		forwardPanel = new ScrollButton(SwingConstants.RIGHT);
		backPanel = new ScrollButton(SwingConstants.LEFT);

		forwardPanel.setVisible(false);
		backPanel.setVisible(false);

		sb.setLayout(new BoxLayout(sb, BoxLayout.LINE_AXIS));

		viewport = new JViewport() {
			@Override
			public Dimension getMinimumSize() {
				// preserve the preferred height of the panel
				Dimension d = getPreferredSize();
				d.width = BTN_SIZE;
				return d;
			}
		};

		viewport.setView(sb.getComponent());
		viewport.setOpaque(false);

		sb.add(backPanel);
		sb.add(viewport);
		sb.add(forwardPanel);

		sb.addComponentListener(this);
	}

	@Override
	public void uninstallUI(JComponent c) {
		if (timer != null && timer.isRunning())
			timer.stop();

		viewport.setView(null);
		sb.removeComponentListener(this);
		sb.removeAll();
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
		JButton b = (JButton) e.getSource();

		if (!b.isEnabled())
			return;

		if (timer == null) {
			timer = new Timer(INIT_DELAY, this);
			timer.setInitialDelay(0);
		} else
			timer.setDelay(INIT_DELAY);

		pressedBtn = b;
		timer.start();
	}

	public void mouseReleased(MouseEvent e) {
		if (timer != null)
			timer.stop();
	}

	/**
	 * Implements scrolling of the toolbar. This method is called
	 * by Swing Timer. Therefore, it should execute quickly and it is safe to
	 * manipulate Swing GUI components from here.
	 */
	public void actionPerformed(ActionEvent e) {
		Point p = viewport.getViewPosition();

		if (pressedBtn == forwardPanel.getButton()) {
			backPanel.setEnabled(true);

			Dimension vs = viewport.getViewSize();
			Dimension es = viewport.getExtentSize();

			if (vs.width - es.width	- p.x > STEP) {
				p.x += STEP;
				viewport.setViewPosition(p);
				accelerate();
			} else {
				p.x = vs.width - es.width;
				viewport.setViewPosition(p);
				forwardPanel.setEnabled(false);
				timer.stop();
			}
		} else if (pressedBtn == backPanel.getButton()) {
			forwardPanel.setEnabled(true);
			if (p.x > STEP) {
				p.x -= STEP;
				viewport.setViewPosition(p);
				accelerate();
			} else {
				p.x = 0;
				viewport.setViewPosition(p);
				backPanel.setEnabled(false);
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

	// ComponentListener

	public void componentHidden(ComponentEvent e) {
		// ignore
	}

	public void componentMoved(ComponentEvent e) {
		// ignore
	}

	public void componentResized(ComponentEvent e) {
		Insets in = sb.getInsets();
		Dimension d = viewport.getView().getPreferredSize();
		if (sb.getWidth() - in.left - in.right < d.width) {
			if (!btnVisible) {
				// backbutton should be disabled when scrolling buttons appear
				backPanel.setEnabled(false);
				forwardPanel.setEnabled(true);

				forwardPanel.setVisible(true);
				backPanel.setVisible(true);
				btnVisible = true;
			} else {
				// check if it is possible to scroll
				// and enable or disable buttons accordingly
				Point p = viewport.getViewPosition();
				backPanel.setEnabled(p.x > 0);
				forwardPanel.setEnabled(p.x + viewport.getExtentSize().width
						< d.width);
			}
		} else {
			if (btnVisible) {
				btnVisible = false;
				forwardPanel.setVisible(false);
				backPanel.setVisible(false);
			}
		}
	}

	public void componentShown(ComponentEvent e) {
		// ignore
	}

	/**
	 * Special JButton for scrolling the toolbar
	 */
	class ScrollButton extends JPanel {

		private static final long serialVersionUID = 1L;

		private final int[][] iconData = {
				// X coordinates
				{3, BTN_SIZE - 3, 3},
				// Y coordinates
				{3, BTN_SIZE / 2, BTN_SIZE - 3}
		};

		private JButton button;

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
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setVisible(false);

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

			g.fillPolygon(iconData[0], iconData[1], iconData[0].length);

			button = new JButton(new ImageIcon(img));
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setFocusable(false);

			button.addMouseListener(ScrollableBarUI.this);

			if (direction == SwingConstants.RIGHT)
				add(Box.createRigidArea(Palette.BUTTON_SPACE));

			add(button);

			if (direction == SwingConstants.LEFT)
				add(Box.createRigidArea(Palette.BUTTON_SPACE));
		}

		public JButton getButton() {
			return button;
		}

		@Override
		public void setEnabled(boolean enabled) {
			button.setEnabled(enabled);
		}
	}
}

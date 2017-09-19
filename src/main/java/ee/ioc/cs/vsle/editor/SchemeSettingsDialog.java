package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.util.FileFuncs;

public class SchemeSettingsDialog extends JDialog {

	public SchemeSettingsDialog( JFrame owner ) {
		super( owner, "Scheme Options", true );
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		init();
		
    	pack();
    	setResizable( false );
    	setVisible( true );
	}

  private void init() {

    setLocationRelativeTo(getParent());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(createSpecSettingsPanel());
    panel.add(createSpecParserPanel());
    panel.add(createPlannerChooserPanel());

    // Scheme background image
    Canvas currentCanvas = Editor.getInstance().getCurrentCanvas();
    if (currentCanvas != null)
      panel.add(createBackgroundImageSettingsPanel(currentCanvas));

    getContentPane().add(panel);
  }

  public JPanel createSpecSettingsPanel() {
    JPanel spec_flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		spec_flow.setBorder( BorderFactory.createTitledBorder("Specification"));
		JPanel spec = new JPanel( );		
		spec.setLayout(new BoxLayout(spec, BoxLayout.Y_AXIS));
		
		List<IFactory<ISpecGenerator>> specs = SpecGenFactory.getInstance().getAllInstances();
		ButtonGroup group = new ButtonGroup();
		for (final IFactory<ISpecGenerator> factory : specs) {
			JRadioButton button = new JRadioButton(factory.getDescription());
			button.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					SpecGenFactory.getInstance().setCurrentSpecGen(factory.getInstance());
				}});
			group.add(button);
			JPanel flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
			flow.add( button );
			spec.add( flow );
			if( factory.getInstance() == SpecGenFactory.getInstance().getCurrentSpecGen() ) {
				button.setSelected( true );
			}
		}
		
		JPanel specRec = new JPanel();
		specRec.setLayout( new BoxLayout( specRec, BoxLayout.X_AXIS ) );
		final JSpinner maxRecDepthSpin = new JSpinner( new SpinnerNumberModel( RuntimeProperties.getMaxRecursiveDeclarationDepth(), 2, 20, 1 ) );
		maxRecDepthSpin.addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				RuntimeProperties.setMaxRecursiveDeclarationDepth( ((Integer)maxRecDepthSpin.getValue()).intValue() );
			}
		} );
		
		final JCheckBox specRecAllowed = new JCheckBox( "Allow recursive declarations", RuntimeProperties.isRecursiveSpecsAllowed() );
		maxRecDepthSpin.setEnabled( specRecAllowed.isSelected() );
		specRecAllowed.addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				boolean selected = specRecAllowed.isSelected();
				
				RuntimeProperties.setRecursiveSpecsAllowed(selected);
				maxRecDepthSpin.setEnabled(selected);
			}
		} );
		specRec.add(specRecAllowed);
		specRec.add(maxRecDepthSpin);
		
		spec.add(specRec);
		
		spec_flow.add(spec);
    return spec_flow;
  }
  
  public JPanel createSpecParserPanel() {
    JPanel spec_flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
    spec_flow.setBorder( BorderFactory.createTitledBorder("Specification Parser"));
    JPanel spec = new JPanel( );    
    spec.setLayout(new BoxLayout(spec, BoxLayout.Y_AXIS));
    
    ButtonGroup group = new ButtonGroup();
    for (final RuntimeProperties.SpecParserKind kind : RuntimeProperties.SpecParserKind.values()) {
      JRadioButton button = new JRadioButton(kind.toString());
      button.addActionListener(new ActionListener(){

        public void actionPerformed(ActionEvent e) {
          RuntimeProperties.setSpecParserKind(kind);
        }});
      group.add(button);
      JPanel flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
      flow.add( button );
      spec.add( flow );
      if( kind == RuntimeProperties.getSpecParserKind() ) {
        button.setSelected( true );
      }
    }
    spec_flow.add(spec);
    return spec_flow;
  }

  public JPanel createPlannerChooserPanel() {
    JPanel plan_flow = new JPanel( new FlowLayout(FlowLayout.LEFT) );
		plan_flow.setBorder( BorderFactory.createTitledBorder("Planner"));
		JPanel plan = new JPanel( );		
		//plan.setLayout(new BoxLayout(plan, BoxLayout.Y_AXIS));
		
		List<Class<? extends IPlanner>> plans = PlannerFactory.getInstance().getAllInstances();
		plan.setLayout( new GridLayout( plans.size(), 0 ) );
		
		ButtonGroup group2 = new ButtonGroup();
		for (final Class<? extends IPlanner> plannerClass : plans) {
		    IPlanner planner = PlannerFactory.getInstance().getPlannerInstance( plannerClass );
			final Component opt = planner.getCustomOptionComponent();
			
			final JRadioButton button = new JRadioButton(planner.getDescription());
			button.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					PlannerFactory.getInstance().setCurrentPlannerClass( plannerClass );
				}});
			group2.add(button);
			if( plannerClass == PlannerFactory.getInstance().getCurrentPlannerClass() ) {
				button.setSelected( true );
			}
			if( opt != null ) {
				JPanel tmp = new JPanel();
				tmp.setLayout(new BoxLayout(tmp, BoxLayout.X_AXIS));
				tmp.add( button );
				final JButton advanced = new JButton( "Advanced" );
				advanced.addActionListener( new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						new JDialog( SchemeSettingsDialog.this, "Advanced Options", true ) {
							@Override
							public void setVisible( boolean b ) {
								if( b ) {
									setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
									setLocationRelativeTo(SchemeSettingsDialog.this);
									getContentPane().add( opt );
									pack();
								}
								super.setVisible( b );
							}
						}.setVisible( true );
					}});
				tmp.add( advanced );//
				plan.add( tmp );
				button.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						if( e.getSource() == button ) {
							advanced.setEnabled( button.isSelected() );
						}
					}});
				advanced.setEnabled( button.isSelected() );
			} else {
				plan.add( button );
			}		
		}		
		plan_flow.add( plan );
    return plan_flow;
  }
    
    private JPanel createBackgroundImageSettingsPanel(final Canvas canvas) {
        final JPanel bgFlow = new JPanel();
        final JButton chooseImage = new JButton("Choose image");
        final JButton clearBg = new JButton("Clear background");

        bgFlow.setBorder(BorderFactory.createTitledBorder("Background image"));

        chooseImage.addActionListener(new ActionListener() {

            private JFileChooser fileChooser;
            
            public void actionPerformed(ActionEvent evt) {
                chooseImage.setEnabled(false);

                if (fileChooser == null) {
                    fileChooser = new JFileChooser(canvas.getPackage().getPath());
                    fileChooser.setFileFilter(new FileFilter() {

                        private final String[] extensions = new String[] {"jpg", "jpeg", "png", "gif", "tif", "tiff"};

                        @Override
                        public boolean accept(File file) {
                            if (file.isDirectory())
                                return file.canRead();

                            String ext = FileFuncs.getExtension(file);

                            if (ext == null)
                                return false;

                            for (int i = 0; i < extensions.length; i++)
                                if (extensions[i].equals(ext))
                                    return file.canRead();

                            return false;
                        }

                        @Override
                        public String getDescription() {
                            return "Image files";
                        }

                    });
                }

                if (fileChooser.showOpenDialog(SchemeSettingsDialog.this) == JFileChooser.APPROVE_OPTION) {
                    final File file = fileChooser.getSelectedFile();
                    if (file == null) {
                        canvas.clearBackgroundImage();
                        clearBg.setEnabled(false);
                        chooseImage.setEnabled(true);
                        return;
                    }

                    // Load the image in background
                    // This code should be revised when Java 1.6 with an official
                    // SwingWorker implementation is released
                    canvas.executor.submit(new Runnable() {
                        public void run() {
                            final ProgressMonitor monitor = new ProgressMonitor(SchemeSettingsDialog.this,
                                    "Loading image...", null, 0, 100);
                            BufferedImage img = null;

                            try {
                                ImageInputStream inStream = ImageIO.createImageInputStream(file);
                                Iterator<ImageReader> readers = ImageIO.getImageReaders(inStream);
                                if (readers.hasNext()) {
                                    final ImageReader reader = readers.next();
                                    reader.setInput(inStream);
                                    reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                                        public void imageComplete(ImageReader source) {
                                            SwingUtilities.invokeLater(new Runnable() {

                                                public void run() {
                                                    monitor.close();
                                                }

                                            });
                                        }

                                        public void imageProgress(ImageReader source, final float percentageDone) {
                                            SwingUtilities.invokeLater(new Runnable() {
                                                
                                                public void run() {
                                                    if (monitor.isCanceled())
                                                        reader.abort();
                                                    else
                                                        monitor.setProgress((int) (percentageDone + .5));
                                                }
                                                
                                            });
                                        }

                                        public void imageStarted(ImageReader source, int imageIndex) {
                                            SwingUtilities.invokeLater(new Runnable() {
                                                
                                                public void run() {
                                                    monitor.setProgress(0);
                                                }
                                                
                                            });
                                        }

                                        public void readAborted(ImageReader source) {
                                            // ignore
                                        }

                                        public void sequenceComplete(ImageReader source) {
                                            // ignore
                                        }

                                        public void sequenceStarted(ImageReader source, int minIndex) {
                                            // ignore
                                        }

                                        public void thumbnailComplete(ImageReader source) {
                                            // ignore
                                        }

                                        public void thumbnailProgress(ImageReader source, float percentageDone) {
                                            // ignore
                                        }

                                        public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnaiIndex) {
                                            // ignore
                                        }
                                        
                                    });

                                    img = reader.read(0);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SwingUtilities.invokeLater((new Runnable() {

                                private BufferedImage image;

                                public void run() {
                                    if (image != null) {
                                        if (!monitor.isCanceled())
                                            canvas.setBackgroundImage(image);
                                        
                                        if (canvas.getBackgroundImage() != null)
                                            clearBg.setEnabled(true);
                                    } else {
                                        JOptionPane.showMessageDialog(Editor.getInstance(),
                                                "Cannot open image file", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                    chooseImage.setEnabled(true);
                                }

                                public Runnable setImage(BufferedImage image) {
                                    this.image = image;
                                    return this;
                                }

                            }).setImage(img));
                        }
                    });
                } else {
                    chooseImage.setEnabled(true);
                }
            }
        });

        bgFlow.add(chooseImage);
        
        clearBg.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                Canvas curCanvas = Editor.getInstance().getCurrentCanvas();
                if (curCanvas != null) {
                    curCanvas.setBackgroundImage(null);
                    clearBg.setEnabled(false);
                }
            }

        });
        
        if (canvas.backgroundImage == null)
            clearBg.setEnabled(false);

        bgFlow.add(clearBg);
       
        return bgFlow;
    }
}

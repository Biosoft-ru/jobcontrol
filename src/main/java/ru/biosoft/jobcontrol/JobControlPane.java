package ru.biosoft.jobcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;


/**
 *  JobControlPane is special panel for using in some dialogs.
 *  It is used for manipulation by long duration processes.
 *  Durable process should provide an {@link JobControl} object .
 *  On the panel there are 3 control buttons:
 *    <ul>
 *      <li>START</li>
 *      <li>STOP</li>
 *      <li>PAUSE</li>
 *    </ul>
 *
 *  Button clicks are translated and then sended to the JobControl.
 *
 */
@SuppressWarnings ( "serial" )
public class JobControlPane extends JPanel
{
    private final Logger log = Logger.getLogger(JobControlPane.class.getName());

    private final BorderLayout       borderLayout1  = new BorderLayout();
    private final MToolBar           toolBar        = new MToolBar();
    private final JPanel             centerPanel    = new JPanel();
    private final FlowLayout         flowLayout1    = new FlowLayout();
    private final JProgressBar       progressBar    = new JProgressBar();
    private final GridBagLayout      gridBagLayout1 = new GridBagLayout();
    private final JPanel             labelPane      = new JPanel();
    private final JLabel             statusLabel    = new JLabel();
    private final JLabel             label1         = new JLabel();
    private final JLabel             remainingLabel = new JLabel("         ");
    private final JLabel             elapsedLabel = new JLabel("         ");
    
    private JobControl         jobControl;
    private JobAction          actionTerminate ;
    private JobAction          actionPause     ;
    private JobAction          actionStart     ;

    /**
     * Constructs panel without JobControl.
     * JobControl should be set later
     * by using {@link #setJobControl(JobControl)}
     */
    public JobControlPane()
    {
        init();
    }

    /**
     * Constructs panel with JobControl.
     */
    public JobControlPane(ClassJobControl jobControl)
    {
        setJobControl(jobControl);
        init();
    }

    public void enableStart( boolean flag )
    {
        actionStart.setEnabled( flag );
    }

    /**
     * Button clicks processor
     */
    private class JCPActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String cmd = e.getActionCommand();
            //System.out.println("cmd="+cmd);

            if (cmd.equals(JobAction.ACTION_START))
                switch (jobControl.getStatus())
                {
                    case JobControl.COMPLETED                  :
                    case JobControl.CREATED                    :
                    case JobControl.TERMINATED_BY_REQUEST      :
                    case JobControl.TERMINATED_BY_ERROR        :
                        (new Thread()
                         {
                             @Override
                            public void run()
                             {
                                 jobControl.run();
                             }
                         }).start();
                        break;
                    case JobControl.PAUSED                     :
                        jobControl.resume();
                        break;
                    case JobControl.RUNNING                    :
                        break;
                }

            else if (cmd.equals(JobAction.ACTION_PAUSE))
                jobControl.pause() ;

            else if (cmd.equals(JobAction.ACTION_TERMINATE))
                jobControl.terminate();
        }
    }


    /**
     * Sets JobControl
     *
     * @param jobCtrl JobControl object
     */
    public void setJobControl(JobControl jobCtrl)
    {
        actionStart.setEnabled(true);
        jobControl = jobCtrl;
        jobControl.addListener(new JobControlListenerAdapter()
        {
            @Override
            public void valueChanged(JobControlEvent event)
            {
                log.log(Level.FINE, "JobControlListener pane : valueChanged " + event.getPreparedness());

                progressBar.setValue(event.getPreparedness());
                long remainingTime = event.getJobControl().getRemainedTime();
                String remainingString = AbstractJobControl.format(remainingTime, "HH:mm:ss");
                remainingLabel.setText(" " + remainingString);
                long elapsedTime = event.getJobControl().getElapsedTime();
                String elapsedString = AbstractJobControl.format(elapsedTime, "HH:mm:ss");
                elapsedLabel.setText(" " + elapsedString);
            }

            @Override
            public void jobStarted(JobControlEvent event)
            {
                statusLabel.setText("Running...");
                actionPause.setEnabled(true);
                actionTerminate.setEnabled(true);
            }

            @Override
            public void jobTerminated(JobControlEvent event)
            {
                int status = jobControl.getStatus();

                String msg = null;

                actionPause.setEnabled(false);
                actionTerminate.setEnabled(false);
                switch (status) {
                    case JobControl.COMPLETED:
                        msg = "Completed";
                        break;
                    case JobControl.TERMINATED_BY_ERROR:
                        msg = "Terminated by error";
                        break;
                    case JobControl.TERMINATED_BY_REQUEST:
                        msg = "Terminated by user";
                        break;
                }
                progressBar.setValue(0);
                statusLabel.setText(msg);
            }

            @Override
            public void jobPaused(JobControlEvent event)
            {
                statusLabel.setText("Paused");
            }

            @Override
            public void jobResumed(JobControlEvent event)
            {
                statusLabel.setText("Continue...");
            }

        });
    }

    private void init()
    {
        TitledBorder titledBorder1 = new TitledBorder( BorderFactory.createEtchedBorder( Color.white, new Color( 148, 145, 140 ) ),
                "Progress status" );
        this.setBorder( titledBorder1 );

        this.setLayout(borderLayout1);
        toolBar.setFloatable(true);
        flowLayout1.setAlignment(FlowLayout.RIGHT);


        ActionListener actionListener = new JCPActionListener();

        actionTerminate = JobAction.createTerminateAction(actionListener);
        actionPause     = JobAction.createPauseAction(actionListener);
        actionStart     = JobAction.createStartAction(actionListener);

        actionTerminate.setEnabled (false);

        actionPause.setEnabled (false);
        actionStart.setEnabled (false);

        addAction(toolBar,actionStart);
        addAction(toolBar,actionPause);
        addAction(toolBar,actionTerminate);

        progressBar.setBorder(BorderFactory.createLoweredBevelBorder());

        centerPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        centerPanel.setSize(new Dimension(250, 100));
        centerPanel.setLayout(gridBagLayout1);

        labelPane.setLayout(new BorderLayout());

        label1.setText("Status:");
        labelPane.setMinimumSize(new Dimension(10, 20));

        statusLabel.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        this.add(centerPanel, BorderLayout.CENTER);



        centerPanel.add(progressBar, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
                                                            ,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        centerPanel.add(labelPane, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0
                                                          ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 0, 0), 0, 0));





        //elapsed time
        JPanel elapsedTimePane = new JPanel(new GridLayout(0, 2));

        elapsedTimePane.add(new JLabel("Elapsed time:"));
        elapsedTimePane.add(elapsedLabel);
        centerPanel.add(elapsedTimePane, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0
                                                          ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 0, 0), 0, 0));

        //remaining time
        JPanel remainingTimePane = new JPanel(new GridLayout(0, 2));

        remainingTimePane.add(new JLabel("Remaining time:"));
        remainingTimePane.add(remainingLabel);
        centerPanel.add(remainingTimePane, new GridBagConstraints(0, 3, 1, 1, 0.5, 0.0
                                                          ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(6, 0, 0, 0), 0, 0));


        labelPane.add(statusLabel, BorderLayout.CENTER);
        labelPane.add(label1, BorderLayout.WEST);
        this.add(toolBar, BorderLayout.SOUTH);
    }


    private  AbstractButton addAction(MToolBar toolBar,Action action)
    {
        JButton b = toolBar.createActionComponent(action);
        b.setAlignmentY(0.5f);
        b.setAction(action);
        b.setActionCommand((String)action.getValue(Action.ACTION_COMMAND_KEY));
        toolBar.add(b);

        return b;
    }

    private static class MToolBar extends JToolBar
    {
        MToolBar()
        {
            setFloatable(true);
            setBorder(BorderFactory.createEtchedBorder());
        }

        @Override
        protected JButton createActionComponent(Action a)
        {
            return super.createActionComponent(a) ;
        }
    }
}
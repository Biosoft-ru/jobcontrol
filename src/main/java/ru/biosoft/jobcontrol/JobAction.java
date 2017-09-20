package ru.biosoft.jobcontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory for creating of special Job Control actions:
 * <ul>
 *   <li><b>STOP</b></li>
 *   <li><b>PAUSE</b></li>
 *   <li><b>TERMINATE</b></li>
 * </ul>
 */
@SuppressWarnings ( "serial" )
public class JobAction extends AbstractAction
{
    /** Pause action key */
    public static final String ACTION_PAUSE        = "pause";
    /** Terminate action key */
    public static final String ACTION_TERMINATE    = "terminate";
    /** Start action key */
    public static final String ACTION_START        = "start";

    protected static final Logger log = Logger.getLogger(JobAction.class.getName());

    /**
     * Protected constructor for internal using.
     * 
     * @param name name of job action
     * @param shortDesc short description of job action
     * @param longDesc long description of job action
     * @param mnemonic hot key
     * @param imageFile image of button/menu
     * @param cmd corresponding string command
     * @param listener listener of action
     */
    protected JobAction(String name, String shortDesc, String longDesc, int mnemonic, String imageFile, String cmd, ActionListener listener)
    {
        putValue(NAME, name);
        putValue(SHORT_DESCRIPTION,  shortDesc );
        putValue(LONG_DESCRIPTION,   longDesc);
        putValue(MNEMONIC_KEY,       mnemonic);
        putValue(ACTION_COMMAND_KEY, cmd );

        URL url =  getClass().getResource("resources/"+imageFile);
        
        if ( url == null )
        {
            log.log(Level.SEVERE, "Error loading resource resources/"+imageFile);
            return;
        }
        
        ImageIcon imageIcon = new ImageIcon(url);
        
        putValue(SMALL_ICON, imageIcon);
        
        if(listener != null)
            addActionListener(listener);
    }

    
    /**
     * Creates "Pause" action.
     * 
     * @param listener listener of this action
     * @return "Pause" action
     */
    public  static JobAction createPauseAction(ActionListener listener)
    {
       return new JobAction("Pause", "Pause process", "Pause process", 'O', "pause.gif" ,ACTION_PAUSE,listener);
    }
    
    /**
     * Creates "Start" action.
     * 
     * @param listener listener of this action
     * @return "Start" action
     */
    public  static JobAction createStartAction(ActionListener listener)
    {
       return new JobAction("Start", "Start process", "Start process", 'O', "start.gif",ACTION_START,listener );
    }
    
    /**
     * Creates "Terminate" action.
     * 
     * @param listener listener of this action
     * @return "Terminate" action
     */
    public static JobAction createTerminateAction(ActionListener listener)
    {
       return new JobAction("Terminate", "Terminate process", "Terminate process", 'O', "terminate.gif" ,ACTION_TERMINATE,listener);
    }
    /**
     * Overridden method for translation action events to the specified listeners
     * @param evt ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        fireActionPerformed(evt) ;
    }

    /**
     * Adds the specified action listener to receive action event .
     */
    public void  addActionListener(ActionListener l)
    {
        if(actionListeners == null)
            actionListeners = new Vector<>();

        actionListeners.add(l);
    }
                                                                 
    /**
     * Notifies all listeners that the action is activised.
     * 
     * @param evt ActionEvent
     */
    protected void  fireActionPerformed(ActionEvent evt)
    {
        ActionEvent event = new ActionEvent(this, evt.getID(), evt.getActionCommand());
        if(actionListeners != null)
        {
            for (int i=0; i<actionListeners.size(); i++)
            {
                ActionListener l = actionListeners.get(i);
                l.actionPerformed(event) ;
            }
        }
    }
    private Vector<ActionListener> actionListeners = null;
}


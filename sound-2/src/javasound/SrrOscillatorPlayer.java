/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 1:26:55 PM
 * To change this template use Options | File Templates.
 */
package javasound;

import javasound.tone.ToneDelegator;

//import srr.tone.ToneDelegator;

public class SrrOscillatorPlayer /*extends MyOscillatorPlayer */ {

    private ToneDelegator tone = new ToneDelegator( 1, 1 );
    private float freq;
    private float amp;
    private boolean isRunning;

    public SrrOscillatorPlayer() {
    }

    public void run() {
        //        super.run();
        if( tone == null ) {
            tone = new ToneDelegator( 1, 1 );
        }
        tone.start();
        isRunning = true;
    }

    public synchronized float getFrequency() {
        return freq;
    }

    public synchronized void setFrequency( float frequency ) {
        tone.setPitch( frequency );
        freq = frequency;
    }

    public synchronized float getAmplitude() {
        return amp;
    }

    public synchronized void setAmplitude( float amplitude ) {
        tone.setVolume( amplitude );
        amp = amplitude;
    }

    public void stop() {
        tone.stop();
        tone = null;
        isRunning = false;
        freq = -1;
        amp = -1;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

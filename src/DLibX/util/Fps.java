package DLibX.util;

/**
 * Handles calculation of a program's framerate.
 * <p>
 * Framerate, also known as frame frequency, is the frequency at which an imaging device displays consecutive images called frames.
 * Frame rate is expressed in frames per second (FPS).
 * This class calculates framerate at a nanosecond resolution, and is capable of weighing new data so the output is smoothed.
 *
 * @author  Marcus Minhorst
 * @version 1.0
 */

public class Fps {
    private double  fps;       // current fps
    private long    duration;  // length of last frame in nanoseconds
    private long    time;      // current stored time
    private boolean first;     // flag to drop last fps
    private double  smoothing; // value factored into smoothing equation

    /**
     * Creates unsmoothed FPS counter.
     * <p>
     * Sets smoothing to 0, which makes FPS be read as the raw, unsmoothed data.
     */

    public Fps() {
        this(0);
    }

    /**
     * Creates smoothed FPS counter.
     * <p>
     * Smoothing factor is how much new data is weighted.
     * The HIGHER the smoothing factor is, the slower the FPS will change.
     * The LOWER the smoothing factor is, the faster the FPS will change.
     * A smoothing factor of ZERO will update the FPS to the most current and unstable value.
     *
     * @throws IllegalArgumentException If the <code>smoothing</code> factor is outside of the range 0.0 to 1.0, exclusive.
     * @param  smoothing Smoothing factor
     */

    public Fps(double smoothing) {
        this.setSmoothing(smoothing);
        this.reset();
    }

    /**
     * Returns current framerate with the smoothing algorithm applied.
     *
     * @return framerate
     */

    public double getFps() {
        return this.fps;
    }

    /**
     * Returns the length of time between the last two updates.
     * <p>
     * The time elapsed between calls to {@link #update()} is calculated with nanosecond precision.
     *
     * @return duration
     */

    public long getFrameDuration() {
        return this.duration;
    }

    /**
     * Returns the length of time since the last update
     * <p>
     * The time elapsed since the last call to {@link #update()} is calculated with nanosecond precision.
     *
     * @return duration
     */

    public long getDuration() {
        long localTime = System.nanoTime();

        if (localTime < this.time) this.reset();

        return this.duration;
    }

    /**
     * Sets smoothing factor.
     * <p>
     * Smoothing factor is how much new data is weighted.
     * The HIGHER the smoothing factor is, the slower the FPS will change.
     * The LOWER the smoothing factor is, the faster the FPS will change.
     * A smoothing factor of ZERO will update the FPS to the most current and unstable value.
     *
     * @throws IllegalArgumentException If the <code>smoothing</code> factor is outside of the range 0.0 to 1.0, exclusive.
     * @param  smoothing Smoothing factor
     */

    public void setSmoothing(double smoothing) {
        if (0 > smoothing || smoothing >= 1) {
            throw new IllegalArgumentException("Smoothing parameter outside of expected range: " + smoothing);
        }

        this.smoothing = smoothing;
    }

    /**
     * Resets all times to ZERO, smoothing factor is maintained.
     */

    public void reset() {
        this.fps = 0;
        this.duration = 0;
        this.time = 0;
        this.first = true;
    }

    /**
     * Calculates and updates FPS relative to the last time it was updated.
     * <p>
     * Fps is calculated with nanosecond precision and is weighted according to the smoothing factor.
     */

    public void update() {
        long localTime = System.nanoTime();

        if (localTime < this.time) this.reset();

        if (this.first && this.time != 0) {
            this.duration = localTime - this.time;
            double localFps = 1e9D / this.duration;
            this.fps = localFps;
            this.first = false;
        } else {
            this.duration = localTime - this.time;
            double localFps = 1e9D / this.duration;
            this.fps = localFps*(1.0-this.smoothing) + this.fps*this.smoothing;
        }

        this.time = localTime;
    }
}

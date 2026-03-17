package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.managers.AudioMan;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.print;
import cwlib.util.Audio;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public abstract class AudioPlayer extends Panel{

    public AudioPlayer() {}

    public AudioPlayer(Vector2f size, RenderMan renderer) {
        super(size, renderer);
        this.init();
    }

    public AudioPlayer(Vector2f pos, Vector2f size, RenderMan renderer) {
        super(pos, size, renderer);
        this.init();
    }

    private Button playStopButton;
    private Slider seekBar;

    private AudioMan.AudioInstance instance;
    private AudioMan.AudioSource source;

    private long duration = 0;

    private boolean pausedForSeeking = false;

    public void init()
    {
        updateAudio();

        this.playStopButton = new Button("playStop", "", this.renderer, this.loader, this.window) {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(action == GLFW.GLFW_PRESS)
                {
                    if(source.isPlaying())
                        source.pause();
                    else
                        source.play();
                }
            }

            @Override
            public void draw(MouseInput mouseInput, boolean overOther) {
                super.draw(mouseInput, overOther);

                if(AudioPlayer.this.source.isPlaying() || pausedForSeeking)
                {
                    float center = this.pos.x + (this.size.x / 2f);
                    float stickWidth = this.size.y * 0.2f;

                    renderer.drawRect((int) (center - (stickWidth * 1.25f)), (int) (this.pos.y + stickWidth), (int) stickWidth, (int) (this.size.y - (stickWidth * 1.5f)), Config.FONT_COLOR);
                    renderer.drawRect((int) (center + (stickWidth * 0.5f)), (int) (this.pos.y + stickWidth), (int) stickWidth, (int) (this.size.y - (stickWidth * 1.5f)), Config.FONT_COLOR);
                }
                else
                {
                    float triangleSize = this.size.y * 0.6f;
                    Vector2f p1 = new Vector2f(pos.x + size.x - size.y / 2f + triangleSize / 2f, pos.y + size.y / 2f);
                    Vector2f p2 = new Vector2f(p1.x - triangleSize, pos.y + size.y / 2f - triangleSize / 2f);
                    Vector2f p3 = new Vector2f(p1.x - triangleSize, pos.y + size.y / 2f + triangleSize / 2f);
                    renderer.drawTriangle(loader, p1, p2, p3, Config.FONT_COLOR);
                }
            }
        };
        this.elements.add(new PanelElement(this.playStopButton, 0.2f));
        this.elements.add(new PanelElement(null, 0.1f));

        this.seekBar = new Slider("seekBar", new Vector2f(), new Vector2f(), this.renderer, this.loader, this.window)
        {
            @Override
            public void onGrabSlider() {
                pausedForSeeking = AudioPlayer.this.source.isPlaying();

                if(pausedForSeeking)
                    AudioPlayer.this.source.pause();
            }

            @Override
            public void onReleaseSlider() {
                AudioPlayer.this.source.setPlaybackSeconds((this.getCurrentValue() / 100f) * (duration / 1000f));
                if(pausedForSeeking)
                    AudioPlayer.this.source.play();
                pausedForSeeking = false;
            }
        };
        this.elements.add(new PanelElement(this.seekBar, 0.7f));
    }

    public void cleanup()
    {
        if(instance != null)
        {
            instance.done(source);
            instance.cleanup();
        }
        if(source != null)
            source.cleanup();
    }

    public void updateAudio()
    {
        cleanup();
        Audio.PCMAudio audio = audio();
        this.duration = audio.calculateDuration();
        this.instance = new AudioMan.AudioInstance(audio);
        this.source = new AudioMan.AudioSource(instance, 1.0f, 1.0f, new Vector3f(), true);
    }

    @Override
    public void resize() {
        super.resize();

        float playStopButtonWidth = this.size.y / this.size.x;
        float gapWidth = 2f / this.size.x;
        float seekBarWidth = 1f - playStopButtonWidth - gapWidth;

        this.elements.get(0).width = playStopButtonWidth;
        this.elements.get(1).width = gapWidth;
        this.elements.get(2).width = seekBarWidth;
    }

    @Override
    public void draw(MouseInput mouseInput, boolean overElement) {

        if(source.isPlaying())
        {
            float playbackPosition = source.getPlaybackSeconds() / (duration / 1000f);

            this.seekBar.setCurrentValue(playbackPosition * 100f);
        }

        super.draw(mouseInput, overElement);
    }

    public abstract Audio.PCMAudio audio();
}

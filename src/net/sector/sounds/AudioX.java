package net.sector.sounds;


import net.sector.Constants;

import org.newdawn.slick.openal.Audio;

import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Wrapper class for slick audio with shorter method names and support for
 * Coords
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class AudioX implements Audio {

	private Audio audio = null;
	private float pauseLoopPosition = 0;
	private boolean looping = false;
	private boolean isLoopPaused = false;
	private boolean playingAsMusic = false;
	private boolean playingAsEffect = false;
	private float playPitch = 1;
	private float playGain = 1;

	public void pauseLoop() {
		if (isPlaying() && looping) {
			pauseLoopPosition = audio.getPosition();
			stop();
			isLoopPaused = true;
		}
	}

	public void resumeLoop() {
		if (looping && isLoopPaused) {
			if (playingAsMusic) {
				audio.playAsMusic(playPitch, playGain, true);
			} else if (playingAsEffect) {
				audio.playAsSoundEffect(playPitch, playGain, true);
			}
			audio.setPosition(pauseLoopPosition);
			isLoopPaused = false;
		}
	}

	/**
	 * AudioX from slick Audio
	 * 
	 * @param audio
	 */
	public AudioX(Audio audio) {
		this.audio = audio;
	}

	@Override
	public void stop() {
		audio.stop();
		//looping = false;
		isLoopPaused = false;
	}

	@Override
	public int getBufferID() {
		return audio.getBufferID();
	}

	@Override
	public boolean isPlaying() {
		return audio.isPlaying();
	}

	@Override
	public boolean isPaused() {
		return audio.isPaused();
	}


	/**
	 * Play effect loop
	 * 
	 * @param pitch effect pitch
	 * @param gain effect volume
	 * @return resource id
	 */
	public int playAsEffectLoop(float pitch, float gain) {
		return playEffectLoop(pitch, gain);
	}

	/**
	 * Play effect loop
	 * 
	 * @param pitch effect pitch
	 * @param gain effect volume
	 * @return resource id
	 */
	public int playEffectLoop(float pitch, float gain) {
		playPitch = pitch;
		playGain = gain;
		looping = true;
		playingAsEffect = true;
		playingAsMusic = false;
		return playAsSoundEffect(pitch, gain, true, Sounds.listener);
	}

	@Override
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		playPitch = pitch;
		playGain = gain;
		looping = loop;
		playingAsEffect = true;
		playingAsMusic = false;
		return playAsSoundEffect(pitch, gain, loop, Sounds.listener);
	}

	@Override
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		return audio.playAsSoundEffect(pitch, gain, loop, x, y, z);
	}

	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param x coord x
	 * @param y coord y
	 * @param z coord z
	 * @return resource index
	 */
	public int playAsSoundEffect(float pitch, float gain, float x, float y, float z) {
		return playAsSoundEffect(pitch, gain, false, x, y, z);
	}

	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @param pos The position of the sound
	 * @return The ID of the source playing the sound
	 */
	public int playAsSoundEffect(float pitch, float gain, boolean loop, Coord pos) {
		return audio.playAsSoundEffect(pitch, gain, loop, (float) pos.x, (float) pos.y, (float) pos.z);
	}

	// shorter
	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @return The ID of the source playing the sound
	 */
	public int playEffect(float pitch, float gain, boolean loop) {
		playPitch = pitch;
		playGain = gain;
		looping = loop;
		playingAsEffect = true;
		playingAsMusic = false;
		return playAsSoundEffect(pitch, gain, loop, Sounds.listener);
	}

	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @param x The x position of the sound
	 * @param y The y position of the sound
	 * @param z The z position of the sound
	 * @return The ID of the source playing the sound
	 */
	public int playEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		return playAsSoundEffect(pitch, gain, loop, x, y, z);
	}

	/**
	 * Play this sound as a sound effect
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @param pos The position of the sound
	 * @return The ID of the source playing the sound
	 */
	public int playEffect(float pitch, float gain, boolean loop, Coord pos) {
		return playAsSoundEffect(pitch, gain, loop, pos);
	}

	/**
	 * Play this sound as a sound effect with linear Z fading
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param hearDist hearing distance
	 * @param loop True if we should loop
	 * @param pos The position of the sound
	 * @return The ID of the source playing the sound
	 */
	public int playEffectLinearZ(float pitch, float gain, float hearDist, boolean loop, Coord pos) {
		float gain2 = Calc.clampf((hearDist - pos.distTo(new Coord(0, 0, 0))) * (1 / hearDist), 0, 1) * gain;
		return playAsSoundEffect(pitch, gain2, loop, new Coord(pos.x, Constants.LISTENER_POS.y - 2, Constants.LISTENER_POS.z));
	}

	// longer

	@Override
	public int playAsMusic(float pitch, float gain, boolean loop) {
		playPitch = pitch;
		playGain = gain;
		looping = loop;
		playingAsEffect = false;
		playingAsMusic = true;
		return audio.playAsMusic(pitch, gain, loop);
	}

	//shorter

	/**
	 * Play this sound as music
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @param loop True if we should loop
	 * @return The ID of the source playing the sound
	 */
	public int playMusic(float pitch, float gain, boolean loop) {
		return playAsMusic(pitch, gain, loop);
	}

	/**
	 * Play this sound as music
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @return The ID of the source playing the sound
	 */
	public int playMusicLoop(float pitch, float gain) {
		return playAsMusic(pitch, gain, true);
	}

	/**
	 * Play this sound as music
	 * 
	 * @param pitch The pitch of the play back
	 * @param gain The gain of the play back
	 * @return The ID of the source playing the sound
	 */
	public int playAsMusicLoop(float pitch, float gain) {
		return playAsMusic(pitch, gain, true);
	}

	@Override
	public boolean setPosition(float position) {
		return audio.setPosition(position);
	}

	@Override
	public float getPosition() {
		return audio.getPosition();
	}

	@Override
	public void release() {
		audio.release();
		audio = null;
	}



}

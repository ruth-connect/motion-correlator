package uk.me.ruthmills.motioncorrelator.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import uk.me.ruthmills.motioncorrelator.mjpeg.MjpegStream;
import uk.me.ruthmills.motioncorrelator.model.Camera;

@Configuration
public class MotionCorrelatorConfig {

	@Bean
	@Scope(BeanDefinition.SCOPE_PROTOTYPE)
	public MjpegStream createMjpegStream(Camera camera) {
		{
			return new MjpegStream(camera);
		}
	}
}

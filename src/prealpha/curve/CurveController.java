package prealpha.curve;

import prealpha.util.Util;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

public class CurveController extends Controller {

		final Curve c;
		final Spatial n;
		final Vector3f point;
		private float progress;
		
		public CurveController( Curve c, Spatial s ) {
			this.c = c;
			this.n = s;
			
			point = new Vector3f();
			
			progress = c.checkProgress(n.getLocalTranslation(), .1f);
		}
		
		@Override
		public void update(float time) {

		}
		
	}

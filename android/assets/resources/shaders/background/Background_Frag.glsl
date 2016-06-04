#version 120

#ifdef GL_ES
precision mediump float;
#endif
uniform sampler2D u_texture;
uniform vec3 u_color;
uniform vec2 u_resolution;
uniform float u_time;
varying vec2 v_textCoord;
varying vec2 v_pos;

// Voronoi noises
// by Pietro De Nicola


#define SCALE		    10.0		// 3.0
#define BIAS   		   +0.0
#define POWER			1.0
#define OCTAVES   		7		// 7
#define SWITCH_TIME 	5.0		// seconds
#define WARP_INTENSITY	0.00	// 0.06
#define WARP_FREQUENCY	10.0
#define ANIM_RATE       0.5

float t = u_time/SWITCH_TIME;

//
// the following parameters identify the voronoi you're watching
// and will change automatically
//
float function 			= 3.0;
bool  multiply_by_F1	= false;
bool  inverse			= true;
float distance_type		= 0.0;


//
// Noise functions
//

vec2 hash( vec2 p )
{
    p = vec2( dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)) );
	return fract(sin(p)*43758.5453);
}

float noise( in vec2 p )
{
    vec2 i = floor( p );
    vec2 f = fract( p );

	vec2 u = f*f*(3.0-2.0*f);

    return mix( mix( dot( hash( i + vec2(0.0,0.0) ), f - vec2(0.0,0.0) ),
                     dot( hash( i + vec2(1.0,0.0) ), f - vec2(1.0,0.0) ), u.x),
                mix( dot( hash( i + vec2(0.0,1.0) ), f - vec2(0.0,1.0) ),
                     dot( hash( i + vec2(1.0,1.0) ), f - vec2(1.0,1.0) ), u.x), u.y);
}

float voronoi( in vec2 x )
{
    vec2 n = floor( x );
    vec2 f = fract( x );

	float F1 = 8.0;
	float F2 = 8.0;


    for( int j=-1; j<=1; j++ )
    for( int i=-1; i<=1; i++ )
    {
        vec2 g = vec2(i,j);
        vec2 o = hash( n + g );

        o = 0.5 + 0.41*sin( (u_time * ANIM_RATE) + 6.2831*o ); // animate

		vec2 r = g - f + o;

		float d = 	distance_type < 1.0 ? dot(r,r)  :				// euclidean^2
				  	distance_type < 2.0 ? sqrt(dot(r,r)) :			// euclidean
					distance_type < 3.0 ? abs(r.x) + abs(r.y) :		// manhattan
					distance_type < 4.0 ? max(abs(r.x), abs(r.y)) :	// chebyshev
					0.0;

		if( d<F1 )
		{
			F2 = F1;
			F1 = d;
		}
		else if( d<F2 )
		{
			F2 = d;
		}
    }

	float c = function < 1.0 ? F1 :
			  function < 2.0 ? F2 :
			  function < 3.0 ? F2-F1 :
			  function < 4.0 ? (F1+F2)/2.0 :
			  0.0;

	if( multiply_by_F1 )	c *= F1;
	if( inverse )			c = 1.0 - c;

    return c;
}

float fbm( in vec2 p )
{
	float s = 0.0;
	float m = 0.0;
	float a = 0.5;

	for( int i=0; i<OCTAVES; i++ )
	{
		s += a * voronoi(p);
		m += a;
		a *= 0.5;
		p *= 2.0;
	}
	return s/m;
}

void main() {
    vec4 pixel = texture2D(u_texture,v_textCoord);
    vec2 uv = (gl_FragCoord.xy / u_resolution.xy) + v_pos;
	float noise = noise(uv * WARP_FREQUENCY);
	uv += WARP_INTENSITY * vec2(noise, -noise);
    float c = POWER * fbm(SCALE * uv) + BIAS;
    vec3 color = c * u_color;
    gl_FragColor = vec4(color, 1.0 + pixel.x);
}
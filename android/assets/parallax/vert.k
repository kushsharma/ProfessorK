attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float travelDistance;

varying vec4 v_color;
varying vec2 t0c, t1c, t2c;

void main()                  
{                            
    v_color = a_color; 
    
    t0c = a_texCoord0;
    t0c.x = t0c.x;
    t0c.y = t0c.y + 0.13;    

    t1c = a_texCoord0;
    t1c.x = t1c.x + travelDistance / 6.5;
    t1c.y = t1c.y - 0.13;    
    
    t2c = a_texCoord0;
    t2c.x = t2c.x + travelDistance / 9.5;
    t2c.y = t2c.y - 0.09;    

    gl_Position =  u_projTrans * a_position;
}
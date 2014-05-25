


attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec2 a_texStart;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;


varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texStart;
void main()           
{                           
    v_color = a_color;
    v_texCoords = a_texCoord0;
    v_texStart = a_texStart;
    gl_Position =  u_projViewTrans * u_worldTrans * a_position; 
}                           
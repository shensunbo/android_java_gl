precision mediump float;
varying vec2 vTexCoord;
uniform sampler2D uTexture;
uniform vec4 vColor;
void main() {
    vec4 texColor = texture2D(uTexture, vTexCoord);
    gl_FragColor = texColor;
}

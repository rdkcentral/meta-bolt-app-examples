SUMMARY = "bolt image for wayland egl test with epoxy"

inherit base-bolt-image

IMAGE_INSTALL += "wayland-egl-test-epoxy"
IMAGE_INSTALL += "wayland-egl-test-input-epoxy"
IMAGE_INSTALL += "xkeyboard-config"

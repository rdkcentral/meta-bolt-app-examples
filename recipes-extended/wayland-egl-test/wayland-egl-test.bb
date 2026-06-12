SUMMARY = "Example wayland egl test application"

LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://UNLICENSE;md5=7246f848faa4e9c9fc0ea91122d6e680"

DEPENDS =  "virtual/egl virtual/libgles2 libxkbcommon"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

S = "${WORKDIR}/git/wayland-egl-test"

SRC_URI = " \
    git://github.com/stagingrdkm/dac-examples-src.git;protocol=https;branch=master;rev=02037873b38f540f6a4b683026791db0f021872c \
"

inherit pkgconfig

TEST_NAME    = "wayland-egl-test"
INPUT_NAME   = "wayland-egl-test-input"

TARGET_CFLAGS     += " -O3 -ggdb3"

TARGET_CC_ARCH    += "${LDFLAGS}"

do_compile () {
  ${CC} ${TARGET_CFLAGS} -o ${TEST_NAME} ${S}/wayland-egl.c $(pkg-config --cflags --libs wayland-client wayland-egl glesv2 egl)
  ${CC} ${TARGET_CFLAGS} -o ${INPUT_NAME} ${S}/wayland-input.c $(pkg-config --cflags --libs wayland-client wayland-egl glesv2 egl xkbcommon)
}

do_install() {
  install -p -m 0755 -D ${S}/${TEST_NAME}    ${D}${bindir}/${TEST_NAME}
  install -p -m 0755 -D ${S}/${INPUT_NAME}   ${D}${bindir}/${INPUT_NAME}
}

PACKAGES =+ "${PN}-input"
FILES:${PN} = "${bindir}/wayland-egl-test"
FILES:${PN}-input = "${bindir}/wayland-egl-test-input"

SUMMARY = "Example firebolt egl test application"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PV = "0.1.0"
PR = "r0"

SRC_URI = "git://github.com/rdkcentral/firebolt-egl-test-app;protocol=https;nobranch=1"
SRCREV = "501bdf7df7cabf9a717936700b311c026f71bff6"

inherit cmake pkgconfig

S = "${WORKDIR}/git"

DEPENDS = "firebolt-cpp-client cairo virtual/egl virtual/libgles2 freetype westeros-simpleshell libxkbcommon"
RDEPENDS:${PN} += "firebolt-cpp-client firebolt-cpp-transport cairo westeros-simpleshell libxkbcommon xkeyboard-config"

FILES:${PN} += " /usr/share/fonts"


SUMMARY = "Vulkan Benchmark"
DESCRIPTION = "vkmark is an extensible Vulkan benchmarking suite with targeted, configurable scenes."
AUTHOR = "Collabora"
HOMEPAGE = "https://github.com/vkmark/vkmark"
BUGTRACKER = "https://github.com/vkmark/vkmark/issues"
SECTION = "graphics"
CVE_PRODUCT = ""
LICENSE = "LGPL2.1"
LIC_FILES_CHKSUM = "file://COPYING-LGPL2.1;md5=4fbd65380cdd255951079008b364516c"

inherit meson pkgconfig features_check

DEPENDS += "assimp glm vulkan-headers vulkan-loader"

REQUIRED_DISTRO_FEATURES = "vulkan"

SRC_URI = "git://github.com/vkmark/vkmark.git;protocol=https;nobranch=1"

SRCREV ?= "36e7d9b2ecf723e876add65534e95f55ec1bc79d"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland xcb', d)}"

PACKAGECONFIG[kms] = "-Dkms=true,-Dkms=false,drm virtual/libgbm"
PACKAGECONFIG[wayland] = "-Dwayland=true,-Dwayland=false,wayland wayland-native wayland-protocols"
PACKAGECONFIG[xcb] = "-Dxcb=true,-Dxcb=false,virtual/libx11 libxcb"

do_write_config:append(){
   sed -i "/\[properties\]/asys_root=\'${STAGING_DIR_TARGET}\'" ${WORKDIR}/meson.cross
}

do_install:append () {
   rm -rf ${D}${datadir}/man
}

FILES:${PN} += "${bindir}"
FILES:${PN} += "${libdir}"
FILES:${PN} += "${datadir}"

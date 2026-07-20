
SRC_URI:append = " file://0001-vulkan-registry-path-prefix.patch"
SRC_URI:append = " file://0002-vkmark-build.patch"
SRC_URI:append = " file://0003-vkmark-xdg-shell.patch"
SRC_URI:append = " file://vkmark-vulkan-test.sh"

do_install:append() {
    install -m 0755 ${WORKDIR}/vkmark-vulkan-test.sh ${D}${bindir}/vkmark-vulkan-test.sh
}

FILES:${PN} += "${bindir}/vkmark-vulkan-test.sh"

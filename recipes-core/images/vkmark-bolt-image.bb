SUMMARY = "vulkan test bolt image"

inherit base-bolt-image

REQUIRED_DISTRO_FEATURES = "vulkan"
IMAGE_INSTALL += "vkmark"


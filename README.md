# meta-bolt-app-examples

Bitbake meta layer extending the **bolt** distro with recipes allowing to build OCI image of app examples.

# Setup and building

See [Setup and building](https://github.com/rdkcentral/meta-bolt-distro/blob/develop/README.md#setup-and-building)
section in the [meta-bolt-distro](https://github.com/rdkcentral/meta-bolt-distro) documentation.

## OCI image building instructions for examples

* Download this repository and enter its root directory.
```
git clone https://github.com/rdkcentral/meta-bolt-distro.git
cd meta-bolt-distro/meta-bolt-app-examples
```

* Setup the build environment.
```
source setup-environment
```

* Start building the OCI images for examples.
```

bitbake wayland-egl-test-bolt-image \
        wayland-egl-test-epoxy-bolt-image  # no multi config

bitbake mc:arm:wayland-egl-test-bolt-image \
        mc:arm64:wayland-egl-test-bolt-image \
        mc:amd64:wayland-egl-test-bolt-image  \
        mc:arm:wayland-egl-test-epoxy-bolt-image \
        mc:arm64:wayland-egl-test-epoxy-bolt-image \
        mc:amd64:wayland-egl-test-epoxy-bolt-image   # requires multi config

```
## Building the example applications as bolt package

To create Bolt packages for the examples, ensure that the base package is available in the package store. Refer to the [building the base bolt package](https://github.com/rdkcentral/meta-bolt-distro?tab=readme-ov-file#building-the-base-bolt-package) section to generate the base package and setting up the package store.


Follow the same steps mentioned in the [OCI image building instructions for examples](#OCI-image-building-instructions-for-examples) chapter to setup and build the example applications, but instead of calling `bitbake <target>`, use the [bolt tool](https://github.com/rdkcentral/bolt-tools/tree/main/bolt) to create bolt packages for the applications.

```
bolt make wayland-egl-test
bolt make wayland-egl-test-input
bolt make wayland-egl-test-epoxy
bolt make wayland-egl-test-input-epoxy
```

## Running bolt packages on device

To run bolt packages on device, use `bolt push` and `bolt run` as explained in [bolt tool usage](https://github.com/rdkcentral/bolt-tools/tree/main/bolt#usage)

```
bolt push <remote> com.rdkcentral.base+0.1.0                # here <remote> is the Hostname or alias of a device accessible via SSH in non-interactive mode

bolt push <remote> com.rdkcentral.wayland-egl-test+0.0.1
bolt push <remote> com.rdkcentral.wayland-egl-test-input+0.0.1
bolt push <remote> com.rdkcentral.wayland-egl-test-epoxy+0.0.1
bolt push <remote> com.rdkcentral.wayland-egl-test-input-epoxy+0.0.1

bolt run <remote> com.rdkcentral.wayland-egl-test+0.0.1
bolt run <remote> com.rdkcentral.wayland-egl-test-input+0.0.1
bolt run <remote> com.rdkcentral.wayland-egl-test-epoxy+0.0.1
bolt run <remote> com.rdkcentral.wayland-egl-test-input-epoxy+0.0.1

```

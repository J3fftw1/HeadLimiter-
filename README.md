# HeadLimiter

Limits the amount of any Slimefun blocks (including Addons) in a chunk.

## Download

[Click here to download](https://blob.build/project/HeadLimiter)

## Configuration

The configuration of limiting is located in `plugins/HeadLimiter/config.yml`, the `block-limits` section.

Here is the default and example configuration:

```yaml
block-limits:
  cargo:
    items-amount: 25
    items:
      - CARGO_NODE_INPUT
      - CARGO_NODE_OUTPUT
      - CARGO_NODE_OUTPUT_ADVANCED
      - CARGO_NODE
      - CARGO_MANAGER
    permission-amount:
      example_1: 50
      example_2: 100
      example_3: 150
```

Each section under `block-limits` is a limit group. All the items in the group share the total limit amount.

- `cargo`: The group name. You can use any name you want.
- `items-amount`: The total amount of items in the group that can be placed in a chunk. Note that only newly placed blocks will be limited. Existing blocks will not be affected.
- `items`: The list of Slimefun item IDs in the group (including Addons).
- `permission-amount`: The permission-based limit. The maximum limit takes effect when the player has multiple permissions.

## Permissions

- `headlimiter.bypass`: Bypass the block limit.

The permission-based limit is defined in the configuration file.

## Commands

Our commands start with `/headlimiter` (aliases: `/hl`).

- `/headlimiter count`: Displays the number of blocks and the limit in current chunk.

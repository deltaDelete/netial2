import type { Schema } from "@markdoc/markdoc/src/types.ts";

export default {
    render: "Attachment",
    description: "Display file attachment",
    children: [],
    attributes: {
        src: {
            type: String,
        },
        label: {
            type: String,
        },
        size: {
            type: String,
        },
    },
    selfClosing: true
} as Schema;

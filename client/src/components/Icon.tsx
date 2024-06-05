import { splitProps } from "solid-js";

export default function Icon(props: { code: string, size?: string, class?: string }) {
    const [picked, other] = splitProps(props, ["code", "size", "class"]);

    return (
        <span {...other} class={"icon" + (picked.class ? " " + picked.class : "")} style={{ "font-size": picked.size }}>{picked.code}</span>
    );
}
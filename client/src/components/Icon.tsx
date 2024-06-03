import { JSX, mergeProps } from "solid-js";

export default function Icon(props: { code: string, size?: string }) {
    const mergedProps = mergeProps(props, {
        class: `font-symbols text-center align-middle`
    });
    return (
        <span {...mergedProps} style={{ "font-size": props.size }}>{props.code}</span>
    );
}
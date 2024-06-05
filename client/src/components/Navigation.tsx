import { AnchorProps } from "@solidjs/router";

export function Navigation(): AnchorProps[] {
    return [
        {
            href: "/",
            class: "button navigation",
            children: "Главная",
            end: true
        },
        {
            href: "/profile",
            class: "button navigation",
            children: "Профиль"
        },
        {
            href: "/messages",
            class: "button navigation",
            children: "Сообщения"
        }
    ];
}
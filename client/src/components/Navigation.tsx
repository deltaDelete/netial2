export function Navigation() {
    return [
        {
            href: "/",
            class: "button navigation",
            children: "Главная"
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
        },
    ];
}
import { Navigate, useLocation, useNavigate } from "@solidjs/router";
import { createResource, Show, Suspense } from "solid-js";
import ApiClient from "@/utils/ApiClient";
import { createQuery } from "@tanstack/solid-query";
import Icon from "@components/Icon";

export default function Confirm() {
    const location = useLocation<{ userId: number } | undefined>();
    const userId = () => location.state?.userId;

    const user = createQuery(() => ({
        queryKey: ["user", userId()],
        queryFn: async () => await ApiClient.instance.users.get(userId()!)
    }));

    return (
        <>
            <Show when={!userId()}>
                <Navigate href={"/"} />
            </Show>
            <div class="container root-container">
                <Suspense fallback={
                    <Icon code={"\ue9d0"} size={"3rem"} class="animate-spin" />
                }>
                    <h1 class="text-2xl text-center heading uppercase">Подтверждение</h1>
                    <Show when={user.isFetched}>
                        <p>Письмо с подтверждением было отправлено на вашу электронную почту.</p>
                        <p>{user.data!.email}</p>
                    </Show>
                </Suspense>
            </div>
        </>
    );
}
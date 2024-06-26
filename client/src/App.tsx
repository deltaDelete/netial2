import { Component, For } from "solid-js";

import { A, RouteSectionProps, useLocation } from "@solidjs/router";
import Header from "@components/Header";
import { AuthContextProvider } from "@/utils/AuthContext";
import { Navigation } from "@components/Navigation";
import { QueryClient, QueryClientProvider } from "@tanstack/solid-query";
import { SolidQueryDevtools } from "@tanstack/solid-query-devtools";

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 300
        }
    }
});

const App: Component<RouteSectionProps> = (props: RouteSectionProps) => {
    return (
        <>
            <QueryClientProvider client={queryClient}>
                <SolidQueryDevtools />
                <AuthContextProvider>
                    {/*@ts-ignore*/}
                    <root>
                        <Header />
                        <div class="flex flex-row items-start self-stretch md:p-4 lg:self-center justify-center">
                            <div
                                class="container flex flex-col sticky top-28 max-lg:hidden basis-1/4 shrink-0 gap-1 my-4">
                                <For each={Navigation()}>{i =>
                                    <A {...i} />
                                }</For>
                            </div>
                            <main>
                                {props.children}
                            </main>
                            <div
                                class="invisible container flex flex-col sticky top-28 max-lg:hidden basis-1/4 shrink-0 gap-1 my-4" />
                        </div>
                        {/*@ts-ignore*/}
                    </root>
                    <footer>
                        Copyright. deltaDelete
                    </footer>
                </AuthContextProvider>
            </QueryClientProvider>
        </>
    );
};

export default App;

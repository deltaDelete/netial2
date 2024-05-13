import type { Component } from "solid-js";

import { RouteSectionProps } from "@solidjs/router";
import Header from "@components/Header";
import { AuthContextProvider } from "@/utils/AuthContext";

const App: Component<RouteSectionProps> = (props: RouteSectionProps) => {
    return (
        <>
            <AuthContextProvider>
                <root>
                    <Header />
                    <main>
                        {props.children}
                    </main>
                </root>
                <footer>
                    TODO: Copyright
                </footer>
            </AuthContextProvider>
        </>
    );
};

export default App;

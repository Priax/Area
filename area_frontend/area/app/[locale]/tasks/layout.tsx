"use client"

import SideMenu from "@/components/navigation/SideMenu";
import { Box, Stack } from "@mui/material";
import Navbar from '@/components/navigation/NavBar';

export default function LocaleLayout({
    children
}: {
    children: React.ReactNode;
}) {
    return (
        <div>
        <SideMenu />
        <Box sx={{ display: 'flex' }}>
                <SideMenu />
                <Navbar />
                {/* Main content */}
                <Box
                    component="main"
                    sx={{
                        flexGrow: 1,
                        backgroundColor: "#F2ECD4",
                        overflow: 'auto',
                    }}
                    >
                    <Stack
                        spacing={2}
                        sx={{
                            alignItems: 'center',
                            px: 3,
                            pt: 2,
                            mt: { xs: 8, md: 0 },
                            height: "100vh"
                        }}
                    >
                        <Box sx={{ width: '100%' }}>
                            {children}
                        </Box>
                    </Stack>
                </Box>
            </Box>
        </div>
    )
}
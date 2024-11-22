"use client"

import { useTranslations } from 'next-intl';
import * as React from 'react';
import type { } from '@mui/x-date-pickers/themeAugmentation';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Button from '@mui/material/Button';
import DownloadIcon from '@mui/icons-material/Download';
import SideMenu from '@/components/navigation/SideMenu';
import Navbar from '@/components/navigation/NavBar';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid2';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import CardActionArea from '@mui/material/CardActionArea';
import RunningImage from '@/public/running.jpg'
import BrandLogosImage from '@/public/services_logos.jpeg'
import { Link } from '@/i18n/routing';
import { useEffect } from "react";
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';

export default function Home() {
    const t = useTranslations('HomePage');
    const router = useRouter();

    useEffect(() => {
        const userToken = getCookie("userToken");
        const userId = getCookie("clientId");

        if (userToken === undefined && userId === undefined) {
            const currentLocale = getCookie("NEXT_LOCALE") || "en";
            router.push(`/${currentLocale}/login`);
        }
    }, [router]);

    const handleDownload = () => {
        console.log('Download clicked');
    };

    return (
        <div>
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
                        position: 'relative',
                    }}
                >
                    <Button
                        variant="contained"
                        startIcon={<DownloadIcon />}
                        href='/client.apk'
                        sx={{
                            position: 'absolute',
                            top: { xs: 80, md: 16 },
                            right: 24,
                            zIndex: 1,
                        }}
                        className='bg-gradient-to-r from-orange-600 to-blue-400'
                    >
                        {t('download') || 'Download'}
                    </Button>

                    <Stack
                        spacing={2}
                        sx={{
                            alignItems: 'center',
                            px: 3,
                            pb: 5,
                            pt: 2,
                            mt: { xs: 8, md: 0 },
                            height: "100vh"
                        }}
                    >
                        <Box sx={{ width: '100%' }}>
                            <Typography component="h2" variant="h6" sx={{ mb: 4, mt: 1 }} className='text-3xl'>
                                {t("title")}
                            </Typography>
                            <Grid container spacing={2} columns={12}>
                                <Grid size={{ xs: 12, sm: 6, xl: 4 }}>
                                    <Card sx={{ maxWidth: 700 }}>
                                        <CardActionArea>
                                            <Link href="/tasks">
                                            <CardMedia
                                                component="img"
                                                height="140"
                                                image={RunningImage.src}
                                                sx={{
                                                    height: 140,
                                                    objectFit: 'cover',
                                                }}
                                                alt="tasks runing"
                                            />
                                            <CardContent>
                                                <Typography gutterBottom variant="h5" component="div">
                                                    {t("task_title")}
                                                </Typography>
                                                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                    {t("task_desc")}
                                                    {" "}2{" "}
                                                    {t("task_desc2")}
                                                </Typography>
                                            </CardContent>
                                            </Link>
                                        </CardActionArea>
                                    </Card>
                                </Grid>
                                <Grid size={{ xs: 12, sm: 6, xl: 4 }}>
                                    <Card sx={{ maxWidth: 700 }}>
                                        <CardActionArea>
                                            <Link href="/services">
                                            <CardMedia
                                                component="img"
                                                height={140}
                                                image={BrandLogosImage.src}
                                                sx={{
                                                    height: 140,
                                                    objectFit: 'cover',
                                                }}
                                                alt="tasks runing"
                                            />
                                            <CardContent>
                                                <Typography gutterBottom variant="h5" component="div">
                                                    {t("services_title")}
                                                </Typography>
                                                <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                                                    {t("services_desc")}
                                                    {" "}1{" "}
                                                    {t("services_desc2")}
                                                </Typography>
                                            </CardContent>
                                            </Link>
                                        </CardActionArea>
                                    </Card>
                                </Grid>
                            </Grid>
                        </Box>
                    </Stack>
                </Box>
            </Box>
        </div>
    );
}
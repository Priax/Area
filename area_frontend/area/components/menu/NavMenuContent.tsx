import * as React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Stack from '@mui/material/Stack';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LibraryAddCheckIcon from '@mui/icons-material/LibraryAddCheck';
import { useTranslations } from 'next-intl';
import { Link } from '@/i18n/routing';
import {usePathname} from '@/i18n/routing';
import LocaleSwitcher from '@/components/translation/LocaleSwitcher';
import HistoryIcon from '@mui/icons-material/History';

export default function NavMenuContext() {
    const t = useTranslations('NavMenu');
    const pathname = usePathname();
    var parts = pathname.split('/');

    const mainListItems = [
        { text: t('home'), icon: <HomeRoundedIcon />, path: "/" },
        { text: t('services'), icon: <DashboardCustomizeIcon />, path: "/services" },
        { text: t('tasks'), icon: <LibraryAddCheckIcon />, path: "/tasks" },
        { text: t('tasks-list'), icon: <HistoryIcon/>, path: "/task-history" },
        { text: t('profil'), icon: <AccountCircleIcon />, path: "/profil" },
    ];
    const secondaryListItems = [
        { text: t('admin'), icon: <AdminPanelSettingsIcon />, path: "/admin" },
    ];

    return (
        <Stack sx={{ flexGrow: 1, p: 1, justifyContent: 'space-between' }}>
            <div>
                <LocaleSwitcher/>
                <List dense>
                    {mainListItems.map((item, index) => (
                        <ListItem key={index} disablePadding
                            sx={{
                                display: 'block',
                                borderRadius: 2,
                                marginBottom: 1,
                                backdropFilter: "blur(5px)",
                                '&:hover': {
                                    bgcolor: "rgba(255, 255, 255, 0.85)",
                                },
                                '&& .Mui-selected': {
                                    bgcolor: "rgba(255, 255, 255, 0.9)",
                                    borderRadius: 2,
                                },
                                bgcolor: "rgba(255, 255, 255, 0.6)",
                            }}
                        >
                            <ListItemButton className='p-0' selected={'/' + parts[1] == item.path}>
                                <Link href={item.path} className='flex w-full p-2 px-4 align-middle text-gray-700'>
                                    {item.icon}
                                    <span className='ms-5'>
                                        {item.text}
                                    </span>
                                </Link>
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            </div>
            <List dense>
                {secondaryListItems.map((item, index) => (
                    <ListItem key={index} disablePadding
                        sx={{
                            display: 'block',
                            borderRadius: 2,
                            marginBottom: 1,
                            backdropFilter: "blur(5px)",
                            '&:hover': {
                                bgcolor: "rgba(255, 255, 255, 0.85)",
                            },
                            '&& .Mui-selected': {
                                bgcolor: "rgba(255, 255, 255, 0.9)",
                                borderRadius: 2,
                            },
                            bgcolor: "rgba(255, 255, 255, 0.6)",
                        }}
                    >
                        <ListItemButton className='p-0' selected={'/' + parts[1] == item.path}>
                            <Link href={item.path} className='flex w-full p-2 px-4 align-middle text-gray-700'>
                                {item.icon}
                                <span className='ms-5'>
                                    {item.text}
                                </span>
                            </Link>
                        </ListItemButton>
                    </ListItem>
                ))}
            </List>
        </Stack>
    );
}


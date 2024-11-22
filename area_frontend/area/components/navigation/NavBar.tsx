import * as React from 'react';
import { styled } from '@mui/material/styles';
import AppBar from '@mui/material/AppBar';
import Stack from '@mui/material/Stack';
import MuiToolbar from '@mui/material/Toolbar';
import { tabsClasses } from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import MenuRoundedIcon from '@mui/icons-material/MenuRounded';
import MenuButton from '@/components/button/MenuButton';
import backgroundImage from '@/public/background.jpg'
import SideMenuMobile from '@/components//menu/SideMenuMobile';

const Toolbar = styled(MuiToolbar)({
    width: '100%',
    padding: '12px',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'start',
    justifyContent: 'center',
    gap: '12px',
    flexShrink: 0,
    [`& ${tabsClasses.flexContainer}`]: {
        gap: '8px',
        p: '8px',
        pb: 0,
    },
});

export default function Navbar() {
    const [open, setOpen] = React.useState(false);

    const toggleDrawer = (newOpen: boolean) => () => {
        setOpen(newOpen);
    };

    return (
        <AppBar
            position="fixed"
            sx={{
                display: { xs: 'auto', md: 'none' },
                boxShadow: 0,
                bgcolor: 'background.paper',
                backgroundImage: `url(${backgroundImage.src})`,
                backgroundPosition: 'center',
                backgroundSize: 'cover',
                borderBottom: '1px solid',
                borderColor: 'divider',
                top: 'var(--template-frame-height, 0px)',
            }}
        >
            <Toolbar variant="regular">
                <Stack
                    direction="row"
                    sx={{
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        flexGrow: 1,
                        width: '100%',
                    }}
                >
                    <Stack direction="row" spacing={1} sx={{ justifyContent: 'center' }}>
                        <Typography variant="h4" component="h1" sx={{ color: 'white', fontWeight: 'bold' }}>
                            AREA
                        </Typography>
                    </Stack>
                    <MenuButton aria-label="menu" onClick={toggleDrawer(true)} className='border-2 border-opacity-50 border-solid border-black rounded-md bg-white bg-opacity-25 backdrop-blur-sm'>
                        <MenuRoundedIcon />
                    </MenuButton>
                    <SideMenuMobile open={open} toggleDrawer={toggleDrawer} />
                </Stack>
            </Toolbar>
        </AppBar>
    );
}

import {useState, useEffect} from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import Drawer, { drawerClasses } from '@mui/material/Drawer';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import backgroundImage from '@/public/background.jpg'
import NavMenuContext from './NavMenuContent';
import { Link } from "@/i18n/routing";
import { getCookie } from "cookies-next";

interface SideMenuMobileProps {
    open: boolean | undefined;
    toggleDrawer: (newOpen: boolean) => () => void;
}

export default function SideMenuMobile({ open, toggleDrawer }: SideMenuMobileProps) {
    
    function getShortTitle(title : string, n = 15) {
        if (title) {
            return title.length > n ? title.substring(0, n) + '...' : title;
        } else {
            return 'Unknown'
        }
    }

    type ProfileType = {
        name: string;
        surname: string;
        email: string;
        password: string;
        phoneNumber: string;
        gender: string;
      };

    const [profile, setProfile] = useState<ProfileType>({
        name: "",
        surname: "",
        email: "",
        password: "",
        phoneNumber: "",
        gender: "",
      });

    const getProfileData = async () => {
        if (getCookie("userToken") === undefined || getCookie("clientId") === undefined ) {
            console.error("No cookies found");
            return;
        }
        try {
          const response = await fetch("http://localhost:8080/users/user", {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: String(getCookie("userToken")),
              UserId: String(getCookie("clientId")),
              RequestId: String(getCookie("clientId")),
            },
          });
    
          if (!response.ok) {
            console.error("Error while fetching", response);
            return;
          }
    
          const data = await response.json();
          setProfile(data);
        } catch (error) {
          console.error("Error", error);
        }
    };

    useEffect(() => {
        getProfileData();
    }, []);

    
    return (
        <Drawer
            anchor="right"
            open={open}
            onClose={toggleDrawer(false)}
            sx={{
                [`& .${drawerClasses.paper}`]: {
                    backgroundColor: 'background.paper',
                    backgroundImage: `url(${backgroundImage.src})`,
                    backgroundPosition: 'right',
                    backgroundSize: 'cover',
                    width: { xs: '80dvw', sm: '40dvw' },
                },
            }}
        >
            <Stack
                sx={{
                    height: '100%',
                }}
            >
                <Stack direction="row" sx={{ p: 2, gap: 1 }}>
                    <Stack
                        direction="row"
                        sx={{ gap: 1, alignItems: 'center', flexGrow: 1, p: 1 }}
                    >
                        <Avatar
                            sizes="small"
                            alt={`${profile.name} ${profile.surname}`}
                            sx={{ width: 36, height: 36 ,bgcolor: "#f6466b" }}
                        >
                            {String(profile.name[0]).toUpperCase()}{String(profile.surname[0]).toUpperCase()}
                        </Avatar>
                        <Typography component="p" variant="h6"
                            sx={{ color: 'white' }}
                        >
                            {`${profile.name} ${profile.surname}`}
                        </Typography>
                    </Stack>
                </Stack>
                <Divider sx={{ bgcolor: "white"}} />
                <Stack sx={{ flexGrow: 1 }}>
                <NavMenuContext />
                <Divider sx={{ bgcolor: "white"}} />
                </Stack>
                <Stack sx={{ p: 2 }}>
                    <Link href="/logout">
                        <Button variant="outlined" className='text-white border-white' fullWidth startIcon={<LogoutRoundedIcon />}>
                            Logout
                        </Button>
                    </Link>
                </Stack>
            </Stack>
        </Drawer>
    );
}

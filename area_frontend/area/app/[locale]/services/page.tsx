"use client"

import { useTranslations } from "next-intl";
import Typography from '@mui/material/Typography';
import { useEffect, useState } from "react";
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';
import Servicetog from '../../../components/serviceToggle/servicetog.jsx'
import { Grid2 } from "@mui/material";
import AccountCard from "@/components/serviceToggle/accountCard.jsx";

export default function servicesPage () {
    const t = useTranslations("ServicePage");

    const router = useRouter();
    const [services, setServices] = useState<string[]>([]);

    const getServices = async () => {
        try {
            const response = await fetch('http://localhost:8080/services/', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'UserID': String(getCookie('clientId')),
                    'Authorization': String(getCookie('userToken')),
                },
            });

            if (!response.ok) {
                console.error(`Error: ${response.status} - ${response.statusText}`);
                return;
            }
            const data = await response.json();
            setServices(data);

        } catch (error) {
            console.error("Fetch error: ", error);
        }
    }

    useEffect(() => {
        const userToken = getCookie("userToken");
        const userId = getCookie("clientId");
  
        if (userToken === undefined && userId === undefined) {
            const currentLocale = getCookie("NEXT_LOCALE") || "en";          
            router.push(`/${currentLocale}/login`);
        }
    }, [router]);

    useEffect(() => {
        getServices();
    }, []);

    return (
        <div style={{backgroundColor: '#F2ECD4'}} className="p-6">
            <Typography component="h2" variant="h6" sx={{ mb: 4 }} className='text-3xl'>
                {t("title")}
            </Typography>      
            <Grid2 container spacing={4} sx={{mt: 10}}>
                <AccountCard serviceName='GMAIL'/>
                <AccountCard serviceName='SPOTIFY'/>
                <AccountCard serviceName='THREADS'/>
            </Grid2>
        </div>

    )
}
import { useGoogleLogin } from '@react-oauth/google';
import { useState } from 'react';
import GoogleIcon from '@mui/icons-material/Google';
import Button from '@mui/material/Button';
import { setCookie, getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';
import {useTranslations} from 'next-intl';

export default function LoginGoogleButton() {
    const router = useRouter();
    const t = useTranslations('Login');

    const handleGoogleLogin = useGoogleLogin({
        onSuccess: async (tokenResponse) => {
            const userInfoResponse = await fetch('https://www.googleapis.com/oauth2/v2/userinfo', {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${tokenResponse.access_token}`,
                },
            });

            if (!userInfoResponse.ok) {
                throw new Error('Failed to fetch user information');
            }

            const userData = await userInfoResponse.json();
            loginToAreaGoogle(tokenResponse.access_token, userData);
        },
        onError: (error) => {
            console.error("Google login error:", error);
        }
    });

    const loginToAreaGoogle = async (token: string, dataUser: any) => {

        console.log(JSON.stringify({
            'email': dataUser['email'],
            'name': dataUser['given_name'],
            'surname': dataUser['family_name'],
            'id': dataUser['id'],
            'token': token,
            'verifiedEmail': dataUser['verified_email']
        }))

        try {
            const response = await fetch('http://localhost:8080/auth/logingoogle', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
              },
              body: JSON.stringify({
                'email': dataUser['email'],
                'name': dataUser['given_name'],
                'surname': dataUser['family_name'],
                'id': dataUser['id'],
                'token': token,
                'verifiedEmail': dataUser['verified_email']
              }),
              cache: 'no-store'
            });
            
            if (!response.ok) {
              console.error("error", response)
              return;
            }
    
            const data = await response.json();
    
            if (getCookie("userToken") !== undefined) {
              console.error("userToken already set");
              return;
            }
            setCookie("userToken", data['right']);
            setCookie("clientId", data['left']);
    
            const currentLocale = getCookie("NEXT_LOCALE") || "en";
            router.push(`/${currentLocale}`);
    
          } catch (error) {
            console.error(error);
          }
    }

    return (
        <Button
            className='mt-2'
            fullWidth
            variant="contained"
            onClick={() => handleGoogleLogin()}
        >
            <GoogleIcon className='me-3'/>
            {t('login_google')}
        </Button>
    );
}

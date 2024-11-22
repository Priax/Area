"use client"

import { useEffect, useState } from 'react';
import { Card, CardContent, CardActions, Typography, Button, List, ListItem, Box, Modal, Input, TextField, Link } from '@mui/material';
import { LogosSpotifyIcon } from '../icons/spotifyLogo';
import { AkarIconsThreadsFill } from '../icons/threadsLogo';
import { LogosGoogleGmail } from '../icons/gmailLogo';
import { LineMdSpotify } from '../icons/spotifyLogoLight';
import CloseIcon from '@mui/icons-material/Close';
import AddIcon from '@mui/icons-material/Add';
import { getCookie } from 'cookies-next';
import { useTranslations } from 'next-intl';

const AccountCard = ({ serviceName }) => {

  const t = useTranslations('ServicePage')

  const [accounts, setAccounts] = useState([]);

  const [accounts2, setAccounts2] = useState([]);

  const [showModal, setShowModal] = useState(false);

  const [mailAPI, setMailAPI] = useState('');

  const account_map = [
    {name : 'SPOTIFY', color: '#1DB954', logo: <LogosSpotifyIcon/>, severalAccounts: false, lightLogo: <LineMdSpotify/>},
    {name: 'GMAIL', color: '#EA4335', logo: <LogosGoogleGmail style={{marginLeft: '0.25em'}}/>, severalAccounts: true},
    {name: 'THREADS', color: '#191818', logo: <AkarIconsThreadsFill/>, severalAccounts: false}
  ];
  
  const getNormalCaps = (name) => {
    return (name.charAt(0).toUpperCase() + name.slice(1).toLowerCase())
  }

  const continueWithService = async (serviceName) => {
    try {
      const response = await fetch(`http://localhost:8080/auth/services/login/${serviceName}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'UserID': String(getCookie('clientId')),
          'Authorization': String(getCookie('userToken')),
        },
        body: JSON.stringify()
      });

      if (!response.ok) {
        console.error("Error while fetching", response);
        return;
      }

      const data = await response.text();

      console.log("Data gotten:", data);

      window.open(data);

    } catch (error) {
      console.error("error", error);
    }
  }

  const continueWithServiceWithMail = async (serviceName, email) => {

    console.log(
      {
        'Content-Type': 'application/json',
        'UserID': String(getCookie('clientId')),
        'Authorization': String(getCookie('userToken')),
      },
      "body", email
    )

    try {
      const response = await fetch(`http://localhost:8080/auth/services/login/${serviceName}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'UserID': String(getCookie('clientId')),
          'Authorization': String(getCookie('userToken')),
        },
        body: email
      })

      if (!response.ok) {
        console.error("error while fetching", response);
      }

      const data = await response.text();

      console.log("data gotten:", data);
      window.open(data);

    } catch (error) {
      console.error("error", error);
    }
  }
  const index = account_map.findIndex(account => account.name === serviceName);
  
  useEffect(() => {
    const fetchData = async () => {
  
      try {
        const response = await fetch('http://localhost:8080/services/info/', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'UserID': String(getCookie('clientId')),
            'Authorization': 'Bearer ' + String(getCookie('userToken')),
            'Service': String(account_map[index]['name']),
          },
        });
  
        if (!response.ok) {
          console.error("Error while fetching", account_map[index]['name'] , await response.text());
          return;
        }
  
        let data;
        if (account_map[index]['name'] === 'GMAIL') {
          data = await response.json();
          setAccounts2(data);
          console.log("Test set:", data);
        } else {
          data = await response.text();
          setAccounts([data]);
        }
        console.log("Data taken:", data);
  
      } catch (error) {
        console.error("Error", error);
      }
    };
    fetchData();
  }, []);

  const logoutAccount = async (serviceName, surname='') => {
    if (serviceName === 'gmail') {
      try {
        const response = await fetch(`http://localhost:8080/auth/services/logout/${serviceName}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'UserID': String(getCookie('clientId')),
            'Authorization': 'Bearer ' + String(getCookie('userToken')),
          },
          body: surname,
        });

        if (!response.ok) {
          console.error("error while fetching", await response.json());
          return;
        }

        const data = await response.text();
        console.log("Data", data);

      } catch (error) {
        console.error("error", error);
      }
    } else {
      try {
        const response = await fetch(`http://localhost:8080/auth/services/logout/${serviceName}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'UserID': String(getCookie('clientId')),
            'Authorization': 'Bearer ' + String(getCookie('userToken')),
          }
        });

        if (!response.ok) {
          console.error("error while fetching", await response.json());
          return;
        }

        const data = await response.text();
        console.log("Data", data);

      } catch (error) {
        console.error("error", error);
      }
    }
    location.reload();
  }


  return (
    <>
    <Card 
      sx={{ 
        width: 375, 
        borderRadius: 2, 
        boxShadow: 6,
        borderWidth: 4,
        borderStyle: 'solid',
        borderColor: account_map[index].color,
        display: 'flex',
        flexDirection: 'column',
        minHeight: 200,
      }}
    >
      <CardContent>
        <Typography variant="h5" component="div" sx={{ mb: 2 }} className='font-bold'>
          <Box display="flex" alignItems="center">                          
              {getCookie("NEXT_LOCALE") === 'en' ? (
                `${getNormalCaps(account_map[index].name)} ${t('Account')}`
              ) : `${t('Account')} ${getNormalCaps(account_map[index].name)}`}
            <Box ml={1}>{account_map[index]?.logo}</Box>
          </Box>
        </Typography>

        {accounts.length > 0 ? (
          <List>
            {accounts.map((account, idx) => (
              <ListItem key={idx} sx={{ py: 0 }}>
                <span>
                  - {account}
                </span>
                <Link
                  onClick={() => {logoutAccount(serviceName.toLowerCase())}}
                  sx={{ color: 'rgb(0, 0, 0)', ml: 1, display: 'flex', alignItems: 'center' }}
                >
                  <CloseIcon sx={{ color: 'rgb(255, 0, 0)' }} />
                </Link>
              </ListItem>
            ))}
          </List>
        ) : accounts2.length > 0 ? (
          <>
          <List>
            {accounts2.map((account2, idx) => (
              <ListItem key={idx} sx={{ py: 0, display: 'flex', alignItems: 'center' }}>
                <span>
                  - {account2.left} ({account2.right})
                </span>
                <Link
                  onClick={() => {logoutAccount(serviceName.toLowerCase(), account2.left)}}
                  sx={{ color: 'rgb(0, 0, 0)', ml: 1, display: 'flex', alignItems: 'center' }}
                >
                  <CloseIcon sx={{ color: 'rgb(255, 0, 0)' }} />
                </Link>
              </ListItem>
            ))}
          </List>
          <br/>
          </>
        ) : (
          <Box 
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexGrow: 1,
              m: 2
            }}
          >
            <Typography color="text.secondary">
              {t("No accounts")} <br/> <br/> <br/>
            </Typography>
          </Box>
        )}
      </CardContent>

      { account_map[index].severalAccounts === true || account_map[index].severalAccounts === false && accounts.length === 0 ? (
        <CardActions sx={{ p: 2, pt: 1 }}>
        <Button
          variant="contained"
          fullWidth
          onClick={() => setShowModal(true)}
          sx={{
            backgroundColor: account_map[index].color,
            '&:hover': {
              backgroundColor: account_map[index].color,
              opacity: 0.9
            }
          }}
          >
          {t('add')} {getNormalCaps(account_map[index].name)} {t('account')}
        </Button>
      </CardActions>
      ) : account_map[index].severalAccounts === false && accounts.length !== 0 ? (
          <></>
      ) : (<></>) 

      }

        <Modal
          open={showModal}
          onClose={() => setShowModal(false)}
          className="flex items-center justify-center"
        >
          <Box
            sx={{
              width: "40%",
              backgroundColor: "#fff",
              borderRadius: "8px",
              borderWidth: 4,
              padding: "24px",
              boxShadow: "0 4px 12px rgba(0, 0, 0, 0.2)",
              borderColor: account_map[index]['color']
            }}
          >
            <Typography id="modal-title" variant="h6" component="h2" align="center" gutterBottom className='text-2xl font-bold'>
              Add {getNormalCaps(account_map[index]['name'])} Account
            </Typography>

            {account_map[index]['severalAccounts'] === true ? (
              <>
                <TextField
                  label={`${getNormalCaps(account_map[index]['name'])} Account Surname`}
                  fullWidth
                  placeholder={`Enter surname of ${getNormalCaps(account_map[index]['name'])} Account...`}
                  value={mailAPI}
                  onChange={(e) => setMailAPI(e.target.value)}
                  sx={{ mt: 5 }}
                />
                <Box display="flex" justifyContent="flex-end" gap={1} sx={{ mt: 5 }}>
                  <Button
                    variant="contained"
                    sx={{
                      backgroundColor: account_map[index]['color']
                    }}
                    onClick={() => continueWithServiceWithMail(account_map[index]['name'].toLowerCase(), mailAPI)}
                  >
                    Submit
                  </Button>
                  <Button
                    onClick={() => setShowModal(false)}
                    sx={{
                      backgroundColor: 'purple',
                      color: 'white'
                    }}
                  >
                    Close
                  </Button>
                </Box>
              </>
            ) : (
              <>
                <Button
                  variant="contained"
                  sx={{
                    mt: 5,
                    backgroundColor: account_map[index]['color']
                  }}
                  onClick={() => continueWithService(account_map[index]['name'].toLowerCase())}
                  fullWidth
                >
                  Continue with {getNormalCaps(account_map[index]['name'])} {account_map[index]['lightLogo'] || account_map[index]['logo']}
                </Button>
                <Box display="flex" justifyContent="flex-end" gap={1} sx={{ mt: 5 }}>
                  <Button
                    onClick={() => setShowModal(false)}
                    sx={{
                      backgroundColor: 'purple',
                      color: 'white'
                    }}
                  >
                    Close
                  </Button>
                </Box>
              </>
            )}
          </Box>
        </Modal>
      </Card>
    </>
  )
};

export default AccountCard;

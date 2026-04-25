/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useState, useEffect } from "react";
import { Form, Input, Modal, Select, Row, Col, message } from "antd";
import { getStudyPrograms } from "@/api/studyProgram";
import { getTahunAjaran } from "@/api/tahun-ajaran";

const { Option } = Select;

const EditKelasForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
}) => {
  const [form] = Form.useForm();
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [tahunAjaranList, setTahunAjaranList] = useState([]);
  const [loadingPrograms, setLoadingPrograms] = useState(false);
  const [loadingTahun, setLoadingTahun] = useState(false);

  const fetchStudyProgramList = async () => {
    setLoadingPrograms(true);
    try {
      const result = await getStudyPrograms();
      if (result.data.statusCode === 200) {
        setStudyProgramList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data program studi");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoadingPrograms(false);
    }
  };

  const fetchTahunAjaranList = async () => {
    setLoadingTahun(true);
    try {
      const result = await getTahunAjaran();
      if (result.data.statusCode === 200) {
        setTahunAjaranList(result.data.content || []);
      } else {
        message.error("Gagal mengambil data tahun ajaran");
      }
    } catch (error) {
      message.error("Terjadi kesalahan: " + error.message);
    } finally {
      setLoadingTahun(false);
    }
  };

  useEffect(() => {
    if (visible) {
      fetchStudyProgramList();
      fetchTahunAjaranList();
    }
  }, [visible]);

  useEffect(() => {
    if (visible && currentRowData) {
      const currentStudyProgram = currentRowData.studyProgram || currentRowData.study_program;
      // tahunAjaran di-serialisasi Jackson dari getTahunAjaran() → field "tahunAjaran" (nested object)
      const currentTahunAjaran = currentRowData.tahunAjaran;

      form.setFieldsValue({
        idKelas: currentRowData.idKelas,
        idStudyProgram: currentStudyProgram?.id,
        idTahunAjaran: currentTahunAjaran?.idTahun ?? null,
        namaKelas: currentRowData.namaKelas,
      });
    }
  }, [visible, currentRowData, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (error) {
      console.error("Validation failed:", error);
    }
  };

  return (
    <Modal
      title="Edit Kelas"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
      width={500}
    >
      <Form form={form} layout="vertical">
        <Form.Item name="idKelas" hidden>
          <Input />
        </Form.Item>

        <Row gutter={16}>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Program Studi:"
              name="idStudyProgram"
              rules={[{ required: true, message: "Silahkan pilih Program Studi" }]}
            >
              <Select
                placeholder="Pilih Program Studi"
                loading={loadingPrograms}
                showSearch
                optionFilterProp="children"
              >
                {studyProgramList.map(({ id, name }) => (
                  <Option key={id} value={id}>
                    {name}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Tahun Ajaran:"
              name="idTahunAjaran"
              rules={[{ required: true, message: "Silahkan pilih Tahun Ajaran" }]}
            >
              <Select
                placeholder="Pilih Tahun Ajaran"
                loading={loadingTahun}
                showSearch
                optionFilterProp="children"
              >
                {tahunAjaranList.map((item) => (
                  <Option key={item.idTahun} value={item.idTahun}>
                    {item.tahunAjaran}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={24} md={24}>
            <Form.Item
              label="Nama Kelas:"
              name="namaKelas"
              rules={[{ required: true, message: "Silahkan isikan Nama Kelas" }]}
            >
              <Input placeholder="Kelas" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default EditKelasForm;
